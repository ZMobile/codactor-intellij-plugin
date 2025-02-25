plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.4"
}

group = "com.codactor"
version = "4.7-BETA"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation("io.github.kju2.languagedetector:language-detector:1.0.5")
}
java {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2022.1.4")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("java", "junit"))
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
        implementation(files("/Users/zantehays/IdeaProjects/java-diff-utils/java-diff-utils/target/java-diff-utils-4.13-SNAPSHOT.jar"))
        implementation("com.github.javaparser:javaparser-core:3.25.10")
        implementation(files("/Users/zantehays/IdeaProjects/jhotdraw/jhotdraw7/target/jhotdraw-7.7.0.jar"))
        implementation("com.github.gumtreediff:gumtree:2.0.0")
        implementation("com.github.gumtreediff:core:3.0.0")
        implementation("com.github.gumtreediff:client:3.0.0")
        implementation("com.github.gumtreediff:gen.srcml:3.0.0")
        implementation("org.bitbucket.cowwoc:diff-match-patch:1.2")
        testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
        //testImplementation("org.junit.platform:junit-platform-engine:1.9.3")
        testImplementation("org.junit.platform:junit-platform-launcher:1.12.0-M1")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
        testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
        testImplementation("org.junit.vintage:junit-vintage-engine:5.8.2") // For JUnit 4 compatibility
        //testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
}
