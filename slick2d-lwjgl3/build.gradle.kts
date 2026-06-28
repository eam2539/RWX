plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

val lwjglVersion = libs.versions.lwjglVersion.get()

dependencies {
    api(files("libs/jogg-0.0.7.jar"))
    api(files("libs/jorbis-0.0.15.jar"))
    api(libs.lwjgl)
    api(libs.lwjgl.glfw)
    api(libs.lwjgl.opengl)
    api(libs.lwjgl.openal)
}

sourceSets {
    main {
        java {
            exclude(
                "org/newdawn/slick/AppGameContainer.java",
                "org/newdawn/slick/AppletGameContainer.java",
                "org/newdawn/slick/CanvasGameContainer.java",
                "org/newdawn/slick/muffin/WebstartMuffin.java",
                "org/newdawn/slick/util/Bootstrap.java",
            )
        }
        resources {
            srcDir("src/main/java")
            exclude("**/*.java")
        }
    }
}
