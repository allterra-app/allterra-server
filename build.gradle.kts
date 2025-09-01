group = "com.allterra"
version = "0.0.1-SNAPSHOT"
description = "allterra-server"

repositories {
	mavenCentral()
}

plugins {
	java
	id("org.springframework.boot") version "3.3.3"
	id("io.spring.dependency-management") version "1.1.6"
	id("com.github.spotbugs") version "6.0.22"
	id("checkstyle")
}

java {
	sourceCompatibility = JavaVersion.VERSION_21
	targetCompatibility = JavaVersion.VERSION_21
}

configurations {
	all {
		exclude(module = "spring-boot-starter-logging")
	}
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-log4j2")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("com.lmax:disruptor:4.0.0")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")
	implementation("org.mapstruct:mapstruct:1.6.2")
	annotationProcessor("org.mapstruct:mapstruct-processor:1.6.2")
	implementation("io.jsonwebtoken:jjwt-api:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

	//database
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.liquibase:liquibase-core:4.29.2")
	implementation("org.postgresql:postgresql")

	//test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("com.h2database:h2:2.3.232")

	//metrics
	implementation("io.micrometer:micrometer-registry-prometheus:1.12.0")

	//checkstyle
	checkstyle("com.puppycrawl.tools:checkstyle:8.42")

	//openapi
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

	//spotbugs plugin dependencies
	spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.13.0")
	spotbugsPlugins("com.mebigfatguy.sb-contrib:sb-contrib:7.6.4")
	implementation("com.github.spotbugs:spotbugs-annotations:4.8.6")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.spotbugsMain {
	reports.create("html") {
		required.set(true)
		outputLocation.set(file("${project.projectDir}/build/reports/spotbugs.html"))
		excludeFilter.set(file("${project.projectDir}/config/spotbugs/spotbugs-exclude.xml"))
		setStylesheet("fancy-hist.xsl")
	}
}

tasks.spotbugsTest {
	reports.create("html") {
		required.set(true)
		outputLocation.set(file("${project.projectDir}/build/reports/spotbugs-test.html"))
		excludeFilter.set(file("${project.projectDir}/config/spotbugs/spotbugs-exclude.xml"))
		setStylesheet("fancy-hist.xsl")
	}
}
