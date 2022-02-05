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
    // Release
    id("net.researchgate.release") version "2.8.1"

    id("com.github.johnrengelman.processes") version "0.5.0"
    id("org.springdoc.openapi-gradle-plugin") version "1.3.3"
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

docker {
    val bootJar by tasks.bootJar
    name = "h3r0ld/morale-assistant-backend"
    tag("latest", "$name:latest")
    tag(version.toString(), "$name:$version")
    files(
            File("$buildDir/libs/${bootJar.archiveFileName.get()}")
    )
}

release {
    preCommitText = "[skip ci]"
}

openApi {
    outputDir.set(file("$projectDir/.."))
    outputFileName.set("open-api.json")
}

springBoot {
    buildInfo()
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

    val springdocVersion = "1.6.4"
    implementation("org.springdoc:springdoc-openapi-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-data-rest:$springdocVersion")

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

    val docker by getting {
        val build by getting
        dependsOn(build)
    }

    val afterReleaseBuild by getting {
        val publish by getting
        val dockerTagsPush by getting

        dependsOn(publish)
        dependsOn(dockerTagsPush)
    }
}

publishing {
    publications {
        val bootJar by tasks.bootJar

        create<MavenPublication>("mavenJava") {
            val openApiFile = "${openApi.outputDir.get()}/${openApi.outputFileName.get()}"
            val dockerComposeFile = "$projectDir/../docker-compose.yml"

            artifacts {
                artifactId = "morale-assistant-backend"
                artifact(bootJar)
                artifact(openApiFile) {
                    extension = "open-api.json"
                }
                artifact(dockerComposeFile) {
                    extension = "docker-compose.yml"
                }
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
