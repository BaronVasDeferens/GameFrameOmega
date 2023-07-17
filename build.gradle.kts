
plugins {
    kotlin("jvm") version "1.6.0"
}

group = "me.skot"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    gradleApi()

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    testImplementation(kotlin("test-junit"))
    implementation(kotlin("stdlib-jdk8"))

    // LibGDX
    val gdxVersion = "1.11.0"
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-bullet:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-freetype:1.11.0")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")

}

tasks.test {
    useJUnit()
}