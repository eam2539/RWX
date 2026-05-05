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
            options.release = 11
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
