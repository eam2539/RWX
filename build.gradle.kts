group = "com.corrodinggames.rts"
version = "1.0.0"

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
