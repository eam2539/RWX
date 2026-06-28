package io.github.rwx.build

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.VarInsnNode
import java.util.zip.ZipFile

abstract class KoolVulkanOverlayPatchTask : DefaultTask() {
    @get:Classpath
    abstract val koolDesktopJar: RegularFileProperty

    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    @TaskAction
    fun patch() {
        val classBytes = readClass(SWAPCHAIN_CLASS_ENTRY)
        val classNode = ClassNode()
        ClassReader(classBytes).accept(classNode, 0)

        val calls = classNode.methods.flatMap { method ->
            method.instructions.toArray()
                .filterIsInstance<MethodInsnNode>()
                .filter { instruction ->
                    instruction.owner == SWAPCHAIN_CREATE_INFO_OWNER &&
                            instruction.name == "compositeAlpha" &&
                            instruction.desc == "(I)L$SWAPCHAIN_CREATE_INFO_OWNER;"
                }
                .map { method to it }
        }
        check(calls.size == 1) {
            "Expected one Kool swapchain compositeAlpha call, found ${calls.size}"
        }

        val (method, call) = calls.single()
        val originalArgument = call.previous
        check(originalArgument.opcode == ICONST_1) {
            "Expected Kool 0.19.0 compositeAlpha argument to be ICONST_1"
        }
        method.instructions.insertBefore(
            originalArgument,
            InsnList().apply {
                add(VarInsnNode(ALOAD, SWAPCHAIN_SUPPORT_LOCAL_INDEX))
                add(
                    MethodInsnNode(
                        INVOKEVIRTUAL,
                        SWAPCHAIN_SUPPORT_OWNER,
                        "getCapabilities",
                        "()L$SURFACE_CAPABILITIES_OWNER;",
                        false,
                    ),
                )
                add(
                    MethodInsnNode(
                        INVOKEVIRTUAL,
                        SURFACE_CAPABILITIES_OWNER,
                        "supportedCompositeAlpha",
                        "()I",
                        false,
                    ),
                )
                add(
                    MethodInsnNode(
                        INVOKESTATIC,
                        COMPOSITE_ALPHA_SELECTOR_OWNER,
                        "select",
                        "(I)I",
                        false,
                    ),
                )
            },
        )
        method.instructions.remove(originalArgument)

        val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
        classNode.accept(writer)
        writeClass(SWAPCHAIN_CLASS_ENTRY, writer.toByteArray())

        patchDrawPipeline()
    }

    private fun patchDrawPipeline() {
        val classNode = ClassNode()
        ClassReader(readClass(DRAW_PIPELINE_CLASS_ENTRY)).accept(classNode, 0)
        val blendInfo = classNode.methods.singleOrNull { method ->
            method.name == "blendInfo" &&
                method.desc ==
                "(Lorg/lwjgl/system/MemoryStack;Lde/fabmax/kool/pipeline/backend/vk/PassEncoderState;)" +
                "Lorg/lwjgl/vulkan/VkPipelineColorBlendStateCreateInfo;"
        } ?: error("Kool DrawPipelineVk blendInfo method was not found")

        val alphaFactorCalls = blendInfo.instructions.toArray()
            .filterIsInstance<MethodInsnNode>()
            .filter { instruction ->
                instruction.owner == COLOR_BLEND_ATTACHMENT_OWNER &&
                    instruction.name == "dstAlphaBlendFactor" &&
                    instruction.desc == "(I)L$COLOR_BLEND_ATTACHMENT_OWNER;"
            }
        check(alphaFactorCalls.size == 3) {
            "Expected three Kool destination alpha blend factors, found ${alphaFactorCalls.size}"
        }

        // Kool 0.19.0 replaces destination alpha for multiply/premultiplied-alpha draws.
        // Transparent texture texels then punch holes through already-rendered UI surfaces.
        alphaFactorCalls.drop(1).forEach { call ->
            val originalArgument = call.previous
            check(originalArgument.opcode == ICONST_0) {
                "Expected Kool destination alpha blend factor argument to be ICONST_0"
            }
            blendInfo.instructions.insertBefore(
                originalArgument,
                IntInsnNode(BIPUSH, VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA),
            )
            blendInfo.instructions.remove(originalArgument)
        }

        val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
        classNode.accept(writer)
        writeClass(DRAW_PIPELINE_CLASS_ENTRY, writer.toByteArray())
    }

    private fun readClass(entryName: String): ByteArray = ZipFile(koolDesktopJar.get().asFile).use { jar ->
        val entry = requireNotNull(jar.getEntry(entryName)) {
            "Kool desktop JAR does not contain $entryName"
        }
        jar.getInputStream(entry).use { it.readBytes() }
    }

    private fun writeClass(entryName: String, bytes: ByteArray) {
        val outputFile = outputDirectory.file(entryName).get().asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeBytes(bytes)
    }

    companion object {
        private const val SWAPCHAIN_CLASS_ENTRY =
            "de/fabmax/kool/pipeline/backend/vk/Swapchain.class"
        private const val SWAPCHAIN_CREATE_INFO_OWNER =
            "org/lwjgl/vulkan/VkSwapchainCreateInfoKHR"
        private const val SWAPCHAIN_SUPPORT_OWNER =
            "de/fabmax/kool/pipeline/backend/vk/PhysicalDevice\$SwapChainSupportDetails"
        private const val SURFACE_CAPABILITIES_OWNER =
            "org/lwjgl/vulkan/VkSurfaceCapabilitiesKHR"
        private const val COMPOSITE_ALPHA_SELECTOR_OWNER =
            "io/github/rwx/VulkanCompositeAlpha"
        private const val SWAPCHAIN_SUPPORT_LOCAL_INDEX = 3
        private const val DRAW_PIPELINE_CLASS_ENTRY =
            "de/fabmax/kool/pipeline/backend/vk/DrawPipelineVk.class"
        private const val COLOR_BLEND_ATTACHMENT_OWNER =
            "org/lwjgl/vulkan/VkPipelineColorBlendAttachmentState"
        private const val VK_BLEND_FACTOR_ONE_MINUS_SRC_ALPHA = 7
    }
}
