plugins {
    java
}
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_22.majorVersion))
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.spring.io")
        content {
            includeGroupByRegex(".*spring.*")
        }
    }
}


dependencies {
    implementation("org.springframework:spring-core:5.3.20")
    implementation("org.springframework:spring-context:5.3.20")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    // Stanford CoreNLP dependencies
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5:models")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5:models-english")

    // Additional dependencies for Stanford NLP tools
    implementation("edu.stanford.nlp:stanford-parser:3.9.2")

    implementation("com.google.guava:guava:32.0.1-jre")
}

tasks.test {
    testLogging {
        showStandardStreams = true
    }

    // Specify the test class to run
    include("**EntityGridFrameworkTest")
}
