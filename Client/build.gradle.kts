plugins {
    id("java")
}

group = "org.abhijith"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.mockito:mockito-core:5.1.1")
    implementation("org.postgresql:postgresql:42.5.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("junit:junit:4.13.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "org.abhijith.Main"
    }
}
