import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
	java
	application
	id("com.gradleup.shadow") version "9.1.0"
}

group = "com.technicjelle"
version = "1.0"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.spongepowered:configurate-hocon:4.2.0")
}

application {
	mainClass.set("com.technicjelle.bluemap3rdvalidator.Main")
}

tasks {
	named<ShadowJar>("shadowJar") {
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
