plugins {
    java
}

group = "com.example"
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
    testImplementation("junit:junit:4.13.2")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8" // Set encoding to UTF-8
}

// Apply common configuration to all subprojects
subprojects {
    apply(plugin = "java")

    dependencies {
        "testImplementation"("junit:junit:4.13.2")
    }

    tasks.withType<Test> {
        useJUnit()
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_22.majorVersion))
        }
    }
}

// Ensure running test in root project runs tests in subprojects
tasks.named<Test>("test") {
    dependsOn(subprojects.map { it.tasks.named<Test>("test") })
}
