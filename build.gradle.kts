plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.13.3"
}

group = "com.codactor"
version = "3.9-BETA"

repositories {
    mavenCentral()
    mavenLocal()
}
dependencies {
    implementation("io.github.kju2.languagedetector:language-detector:1.0.5")
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("java"))
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    patchPluginXml {
        sinceBuild.set("220.*")
        untilBuild.set("300.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    dependencies {
        implementation("org.eclipse.jetty:jetty-server:11.0.16")
        implementation("org.eclipse.jetty:jetty-servlet:11.0.16")
        compileOnly("jakarta.servlet:jakarta.servlet-api:6.0.0")
        implementation("com.google.inject:guice:5.1.0")
        implementation("com.google.inject.extensions:guice-assistedinject:5.1.0")
        implementation("com.norconex.language:langdetect:1.3.0")
        implementation(fileTree("libs") { include("*.jar") })
        implementation("org.openjfx:javafx-swing:21-ea+5")
        implementation("io.github.java-diff-utils:java-diff-utils:4.12")
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
