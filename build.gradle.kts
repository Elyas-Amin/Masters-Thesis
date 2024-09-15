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
}

dependencies {
    implementation("com.google.code.gson:gson:2.8.9")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8" // Set encoding to UTF-8
}

// Apply common configuration to all subprojects
subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        // Only include the Spring repository for specific subprojects like coherenceModels
        maven {
            url = uri("https://repo.spring.io")
            content {
                includeGroupByRegex(".*spring.*")
            }
        }
    }

    dependencies {
        // Common JUnit 5 testing dependencies
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")

        // AssertJ
        testImplementation("org.assertj:assertj-core:3.24.2")

        // JUnit Platform Suite for running test suites
        testImplementation("org.junit.platform:junit-platform-suite-api:1.7.0")
        testImplementation("org.junit.platform:junit-platform-suite-engine:1.7.0")

        // SLF4J API
        implementation("org.slf4j:slf4j-api:1.7.30")
        // Logback (SLF4J's native implementation)
        implementation("ch.qos.logback:logback-classic:1.2.11")
    }

    tasks.test {
        useJUnitPlatform()
        testLogging {
            showStandardStreams = true
        }
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_22.majorVersion))
        }
    }
}

tasks.named<Test>("test") {
    dependsOn(subprojects.map { it.tasks.named<Test>("test") })
}
