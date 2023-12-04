plugins {
	id("java")
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

tasks.test {
	useJUnitPlatform()
}
