plugins {
  val kotlinVersion = "2.2.21"
  kotlin("jvm") version kotlinVersion
  kotlin("plugin.spring") version kotlinVersion
  id("org.springframework.boot") version "4.0.0"
  id("io.spring.dependency-management") version "1.1.7"
}

group = "de.welcz.samples"
version = "0.0.1-SNAPSHOT"

java {
  toolchain {
    languageVersion = JavaLanguageVersion.of(21)
  }
}

repositories {
  mavenCentral()
}

dependencies {
  val kotestVersion = "6.0.0"
  val arrowVersion = "2.1.0"

  implementation("org.springframework.boot:spring-boot-starter-actuator")
  implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
  implementation("org.springframework.boot:spring-boot-starter-webflux")
  implementation("tools.jackson.module:jackson-module-kotlin")
  implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
  implementation("io.github.microutils:kotlin-logging:3.0.0")
  implementation("io.arrow-kt:arrow-core:$arrowVersion")
  implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.14")

  developmentOnly("org.springframework.boot:spring-boot-devtools")
  developmentOnly("org.springframework.boot:spring-boot-docker-compose")

  testImplementation("org.springframework.boot:spring-boot-starter-webflux-test")
  testImplementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
  testImplementation("io.projectreactor:reactor-test")
  testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
  testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
  testImplementation("io.kotest:kotest-property:$kotestVersion")
  testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
  testImplementation("io.kotest:kotest-extensions-spring:$kotestVersion")
  testImplementation("com.ninja-squad:springmockk:4.0.2")
  testImplementation("org.springframework.boot:spring-boot-testcontainers")
  testImplementation("org.testcontainers:mongodb:1.21.3")
}

kotlin {
  compilerOptions {
    freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}
