import io.github.rwx.build.I18nGenerationExtension
import io.github.rwx.build.I18nGenerationSupport

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.application) apply false
}

group = property("group") as String
version = property("version") as String

allprojects {
    group = rootProject.group
    version = rootProject.version
}

I18nGenerationSupport.register(rootProject)

configure<I18nGenerationExtension> {
    inputFormat = "properties"
}

subprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        if (project.path != ":android") {
            options.release = 25
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
