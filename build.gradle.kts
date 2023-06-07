import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jetbrains.compose") version "1.2.0"
}

group = "com.numq"
version = "1.0"


repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

val composeVersion = "1.0.0"
val coroutinesVersion = "1.6.4"
val socketVersion = "1.5.3"
val koinVersion = "3.2.0"
val jsonVersion = "20220320"
val utilVersion = "1.6.0"
val codecVersion = "1.9"
val jmdnsVersion = "3.5.1"
val junitVersion = "1.8.0"
val mockkVersion = "1.9.1"

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.java-websocket:Java-WebSocket:$socketVersion")
    implementation("io.insert-koin:koin-core:$koinVersion")
    implementation("it.czerwinski:kotlin-util:$utilVersion")
    implementation("org.json:json:$jsonVersion")
    implementation("commons-codec:commons-codec:$codecVersion")
    implementation("org.jetbrains.compose.material:material-icons-extended-desktop:$composeVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutinesVersion")
    implementation("org.jmdns:jmdns:$jmdnsVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:$junitVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

compose.desktop {
    application {
        mainClass = "application/ApplicationKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "stash-desktop"
            packageVersion = "1.0.0"
        }
    }
}