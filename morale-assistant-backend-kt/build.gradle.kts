import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.4.21"
    // Spring
    id("org.springframework.boot") version "2.4.1"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    // Docker
    id("com.palantir.docker") version "0.26.0"
    // Kotlin
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    // Maven publish
    id("maven-publish")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

docker {
    val bootJar by tasks.bootJar
    name = "h3r0ld/morale-assistant-backend:$version"
    files(
            File("$buildDir/libs/${bootJar.archiveFileName.get()}"),
            File(System.getenv("GOOGLE_CREDENTIALS_FILE_PATH") ?: "$projectDir/src/main/resources/google_credentials.json" )
    )
}

val azureDevOpsRepoUrl: String by extra
val azureDevOpsUsername: String by extra
val azureDevOpsPassword: String by extra

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://repo.spring.io/milestone")
    }
    maven {
        name = "Azure DevOps Maven Artifactory"
        url = uri(azureDevOpsRepoUrl)
        credentials {
            username = azureDevOpsUsername
            password = azureDevOpsPassword
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation(springBootStarter("data-rest"))
    implementation(springBootStarter("cache"))
    implementation(springBootStarter("data-jpa"))
    implementation(springBootStarter("validation"))
    implementation(springBootStarter("security"))

    implementation(springBootModule("configuration-processor"))

    runtimeOnly(springBootStarter("aop"))

    implementation("org.springframework.retry:spring-retry:1.3.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    val googleCloudTTSVersion = "0.117.0-beta"
    implementation("com.google.cloud:google-cloud-texttospeech:$googleCloudTTSVersion")

    val maryTTSVersion = "5.2"
    implementation("de.dfki.mary:voice-cmu-slt-hsmm:$maryTTSVersion")

    kapt("org.hibernate:hibernate-jpamodelgen:5.4.27.Final")

    testImplementation(springBootStarter("test"))
    testImplementation("com.h2database:h2")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")


    runtimeOnly("org.postgresql:postgresql")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }
}

publishing {
    publications {
        val bootJar by tasks.bootJar

        create<MavenPublication>("mavenJava") {
            artifact(bootJar)
        }

        create<MavenPublication>("DockerFile") {
            val dockerFile = "$projectDir/Dockerfile"
            artifact(dockerFile) {
                artifactId = "Dockerfile"
            }
        }

        create<MavenPublication>("dockerCompose") {
            val dockerComposeFile = "$projectDir/../docker-compose.yml"
            artifact(dockerComposeFile) {
                artifactId = "docker-compose"
                extension = "yml"
            }
        }
    }

    repositories {
        maven {
            name = "azure-devops"
            url = uri(azureDevOpsRepoUrl)
            credentials {
                username = azureDevOpsUsername
                password = azureDevOpsPassword
            }
        }
    }
}

fun DependencyHandler.springBootModule(module: String, version: String? = null): Any =
    "org.springframework.boot:spring-boot-$module${version?.let { ":$version" } ?: ""}"

fun DependencyHandler.springBootStarter(module: String, version: String? = null): Any =
    springBootModule(module = "starter-$module", version = version)
