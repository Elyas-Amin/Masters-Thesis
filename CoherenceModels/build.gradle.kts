plugins {
    java
}
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.spring.io/milestone")
    }
    maven {
        url = uri("https://repo.spring.io/snapshot")
    }
}

dependencies {
    // Add your project's dependencies here
    implementation("org.springframework:spring-core:5.3.20")
    implementation("org.springframework:spring-context:5.3.20")

    // JUnit 5 for testing
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

    // Stanford CoreNLP dependencies
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5:models")
    implementation("edu.stanford.nlp:stanford-corenlp:4.5.5:models-english")

    // Additional dependencies for Stanford NLP tools
    implementation("edu.stanford.nlp:stanford-parser:3.9.2")

}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        events("passed", "skipped", "failed")
    }

    // Specify the test class to run
    include("**/src/test/EntityGridFrameworkTest")
}
