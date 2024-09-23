plugins {
    java
    id("com.diffplug.spotless") version "6.0.0"
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint("0.45.2").userData(mapOf("indent_size" to "4", "continuation_indent_size" to "4"))
        licenseHeader("/* (C) Copyright 2024 */")
    }

    kotlinGradle {
        target("**/*.kts")
        ktlint("0.45.2")
    }

    format("misc") {
        target("**/*.md", "**/*.gitignore")
        trimTrailingWhitespace()
        endWithNewline()
    }
}

version = "1.0-SNAPSHOT"

dependencies {
    implementation("org.springframework:spring-core:5.3.20")
    implementation("org.springframework:spring-context:5.3.20")

    // Stanford CoreNLP and related dependencies
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5:models")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5:models-english")
    implementation("edu.stanford.nlp:stanford-parser:3.9.2")

    // Guava
    implementation("com.google.guava:guava:32.0.1-jre")

    // Mockito for mocking
    testImplementation("org.mockito:mockito-core:3.7.7")
    testImplementation("org.mockito:mockito-junit-jupiter:3.7.7")
}

tasks.test {
    useJUnitPlatform()
}
