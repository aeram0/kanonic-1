plugins {
    kotlin("jvm") version "1.6.21"
}

version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":kanonic-runtime"))
    implementation(project(":kanonic-syntax"))
}

val generatedSrc = "$buildDir/generated-src/main/kotlin"

sourceSets {
    main {
        java.srcDir(generatedSrc)
    }
}

kotlin.sourceSets {
    main {
        kotlin.srcDir(generatedSrc)
    }
}

val generate = tasks.register<Exec>("generate") {
    dependsOn(":kanonic-syntax:install")
    workingDir("$projectDir/..")
    commandLine("kanonic-syntax/build/install/kanonic-generate/bin/kanonic-generate", "kanonic-parser/build/generated-src/main/kotlin")
}

tasks.compileKotlin {
    dependsOn(generate)
}
