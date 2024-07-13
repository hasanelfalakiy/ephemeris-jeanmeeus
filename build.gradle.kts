plugins {
    // Apply the org.jetbrains.kotlin.jvm plugin to add support for Kotlin
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    // Apply the application plugin to add support for building a CLI application in Java
    application
}

group = "com.hasanelfalakiy"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.1")
}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

application {
    mainClass.set("com.andihasan7.lib.ephemeris.jeanmeeus.AppKt")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
