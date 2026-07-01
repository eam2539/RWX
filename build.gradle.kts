plugins {
    alias(libs.plugins.kotlin.jvm) apply false
}

group = property("group") as String
version = property("version") as String

allprojects {
    group = rootProject.group
    version = rootProject.version
}

subprojects {
    if (project.name != "android") {
        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.release = 17
        }
    } else {
        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
