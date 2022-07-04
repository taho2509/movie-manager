import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.7.0"
	kotlin("plugin.spring") version "1.7.0"
	kotlin("kapt") version "1.7.0"
}

group = "com.hobbie.movie"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
	maven {
		url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("io.github.serpro69:kotlin-faker:1.11.0")
	implementation ("com.google.code.gson:gson:2.9.0")
	implementation("io.nats:jnats:2.15.4")
	implementation ("io.nats:java-nats-streaming:2.2.3")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.slf4j:slf4j-api:1.7.36")
	implementation("ch.qos.logback:logback-core:1.2.11")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.12.4")
	testImplementation ("org.testcontainers:testcontainers:1.17.3")
	testImplementation ("org.testcontainers:mongodb:1.17.3")
	testImplementation("org.testcontainers:junit-jupiter:1.17.2")
	testImplementation ("org.testcontainers:mockserver:1.17.2")
	implementation("org.mock-server:mockserver-client-java:5.13.2")
	kapt("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.create("printVersionName") {
	doLast { println(version) }
}