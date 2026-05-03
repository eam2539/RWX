group = property("group") as String
version = property("version") as String

allprojects {
    group = rootProject.group
    version = rootProject.version
}

subprojects {
    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release = 11
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
