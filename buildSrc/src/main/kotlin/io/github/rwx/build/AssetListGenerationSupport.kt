package io.github.rwx.build

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.tasks.TaskProvider

data class AssetListGeneration(
    val task: TaskProvider<Task>,
    val outputFile: RegularFile,
)

object AssetListGenerationSupport {
    val runtimeAssetExcludes = listOf(
        "font/NotoSansCJKsc-Regular.otf",
        "font/ZenDots-Regular.ttf",
    )
    private val assetListGenerationExcludes = listOf("assets.txt") + runtimeAssetExcludes

    fun register(project: Project): AssetListGeneration {
        val rootProject = project.rootProject
        val assetsDir = rootProject.layout.projectDirectory.dir("assets")
        val generatedAssetsFile = rootProject.layout.projectDirectory.file("assets/assets.txt")

        val task = rootProject.tasks.findByName("generateAssetList")?.let {
            rootProject.tasks.named("generateAssetList")
        } ?: rootProject.tasks.register("generateAssetList") {
            inputs.dir(assetsDir)
            outputs.file(generatedAssetsFile)

            doLast {
                val assetsFolder = assetsDir.asFile
                val assetsFile = generatedAssetsFile.asFile
                val assetEntries = rootProject.fileTree(assetsFolder) {
                    exclude(assetListGenerationExcludes)
                }
                    .files
                    .map { file -> assetsFolder.toPath().relativize(file.toPath()).toString().replace('\\', '/') }
                    .sorted()

                assetsFile.parentFile.mkdirs()
                assetsFile.writeText(
                    buildString {
                        assetEntries.forEach { appendLine(it) }
                    }
                )
            }
        }

        return AssetListGeneration(task, generatedAssetsFile)
    }
}
