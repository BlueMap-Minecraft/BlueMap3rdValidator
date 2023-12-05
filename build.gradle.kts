import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	java
	application
	id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.technicjelle"
version = "1.0"

repositories {
	mavenCentral()
	maven("https://repo.spongepowered.org/maven/")
}

dependencies {
	testImplementation(platform("org.junit:junit-bom:5.9.1"))
	testImplementation("org.junit.jupiter:junit-jupiter")
	implementation("org.spongepowered:configurate-hocon:4.2.0-SNAPSHOT")
}

application {
	mainClass = "com.technicjelle.bluemap3rdvalidator.Main"
}

tasks {
	named<ShadowJar>("shadowJar") {
		archiveBaseName.set("shadow")
		mergeServiceFiles()
		manifest {
			attributes(mapOf("Main-Class" to "com.technicjelle.bluemap3rdvalidator.Main"))
		}
	}

	build {
		dependsOn(shadowJar)
	}

	test {
		useJUnitPlatform()
	}
}
