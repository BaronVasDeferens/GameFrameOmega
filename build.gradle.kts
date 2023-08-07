
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

    // Moshi
    implementation("com.squareup.moshi:moshi-kotlin:1.14.0")
    implementation("com.squareup.moshi:moshi-kotlin-codegen:1.14.0")

    // LibGDX
    val gdxVersion = "1.11.0"
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-platform:$gdxVersion:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-tools:1.11.0")
    implementation("com.badlogicgames.gdx:gdx-bullet:$gdxVersion")
    implementation("com.badlogicgames.gdx:gdx-freetype:1.11.0")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:$gdxVersion:natives-desktop")

    // Testing
    testImplementation ("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnit()
}