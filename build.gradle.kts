import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    id("org.jetbrains.compose") version "0.4.0"
}

group = "com.numq"
version = "1.0"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

val coroutines = "1.6.2"
val websocket = "1.5.3"
val koin = "3.2.0"
val json = "20220320"
val util = "1.6.0"
val codec = "1.9"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines")
    implementation("org.java-websocket:Java-WebSocket:$websocket")
    implementation("io.insert-koin:koin-core:$koin")
    implementation("it.czerwinski:kotlin-util:$util")
    implementation("org.json:json:$json")
    implementation("commons-codec:commons-codec:$codec")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "stash-desktop"
            packageVersion = "1.0.0"
        }
    }
}