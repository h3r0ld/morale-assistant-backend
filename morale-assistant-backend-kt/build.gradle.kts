import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.6.10"
    // Spring
    id("org.springframework.boot") version "2.5.4"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    // Docker
    id("com.palantir.docker") version "0.26.0"
    // Kotlin
    kotlin("jvm") version kotlinVersion
    kotlin("kapt") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
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
    forkProperties.set("-Dspring.profiles.active=open-api")
    outputDir.set(file("$projectDir/.."))
    groupedApiMappings.putAll(mapOf(
        "http://localhost:8080/v3/api-docs/public" to "open-api.public.json",
        "http://localhost:8080/v3/api-docs/admin" to "open-api.admin.json"
    ))
}

springBoot {
    buildInfo()
}

val azureDevOpsRepoUrl: String by extra
val azureDevOpsUsername: String by extra
val azureDevOpsPassword: String by extra

repositories {
    mavenCentral()
    // MaryTTS
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk7"))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))

    implementation(springBootStarter("web"))
    implementation(springBootStarter("cache"))
    implementation(springBootStarter("data-jpa"))
    implementation(springBootStarter("validation"))
    implementation(springBootStarter("security"))
    implementation(springBootStarter("actuator"))

    implementation(springBootModule("configuration-processor"))

    runtimeOnly(springBootStarter("aop"))

    implementation("io.awspring.cloud:spring-cloud-starter-aws:2.3.3")
    implementation("io.awspring.cloud:spring-cloud-starter-aws-parameter-store-config:2.3.3")

    implementation("org.springframework.retry:spring-retry:1.3.0")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    val googleCloudTTSVersion = "0.117.0-beta"
    implementation("com.google.cloud:google-cloud-texttospeech:$googleCloudTTSVersion")

    val maryTTSVersion = "5.2"
    implementation("de.dfki.mary:voice-cmu-slt-hsmm:$maryTTSVersion")

    val springdocVersion = "1.6.4"
    implementation("org.springdoc:springdoc-openapi-ui:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-data-rest:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-security:$springdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springdocVersion")

    kapt("org.hibernate:hibernate-jpamodelgen:5.4.27.Final")

    testImplementation(springBootStarter("test"))
    testImplementation("com.h2database:h2")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
}

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    withType<org.springdoc.openapi.gradle.plugin.OpenApiGeneratorTask> {
        inputs.files(*bootJar.get().outputs.files.toList().toTypedArray())
    }

    withType<Test> {
        useJUnitPlatform()
    }

    val docker by getting {
        val build by getting
        dependsOn(build)
    }

    val afterReleaseBuild by getting {
        val dockerTagsPush by getting
        dependsOn(dockerTagsPush)
    }
}

fun DependencyHandler.springBootModule(module: String, version: String? = null): Any =
    "org.springframework.boot:spring-boot-$module${version?.let { ":$version" } ?: ""}"

fun DependencyHandler.springBootStarter(module: String, version: String? = null): Any =
    springBootModule(module = "starter-$module", version = version)
