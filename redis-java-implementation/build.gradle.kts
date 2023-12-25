plugins {
    id("java")
}

group = "com.anirln"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.8.0")

    implementation("com.h2database:h2:2.2.224")
    implementation("io.netty:netty-handler:4.1.104.Final")
}

tasks.test {
    useJUnitPlatform()
}