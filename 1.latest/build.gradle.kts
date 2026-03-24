@file:Suppress("UnstableApiUsage")

plugins {
	id("net.fabricmc.fabric-loom")
	id("io.github.p03w.machete")
}

val minecraft = "26.1"
val minecraftFriendly = "26.1"
val modmenu = "18.0.0-alpha.6"
val fapi = "0.144.0"
group = project.property("maven_group") as String
version = "${project.property("version")}+$minecraftFriendly"
base.archivesName = "AxolotlClient"

loom {
	accessWidenerPath.set(file("src/main/resources/axolotlclient.accesswidener"))
	mods {
		create("axolotlclient") {
			sourceSet("main")
		}
		create("axolotlclient-test") {
			sourceSet("test")
		}
	}
}

repositories {
	maven("https://maven.noxcrew.com/public")
	maven("https://maven.enginehub.org/repo/")
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraft")
	implementation("net.fabricmc:fabric-loader:${project.property("fabric_loader")}")

	implementation("net.fabricmc.fabric-api:fabric-api:$fapi+$minecraftFriendly")

	implementation("io.github.axolotlclient:AxolotlClient-config:${project.property("config")}+$minecraftFriendly")
	include("io.github.axolotlclient:AxolotlClient-config:${project.property("config")}+$minecraftFriendly")
	implementation(include("io.github.axolotlclient:AxolotlClient-config-rounded:${project.property("config")}+$minecraftFriendly")!!)

	compileOnly("com.terraformersmc:modmenu:$modmenu")

	implementation(include(project(path = ":common", configuration = "shadow"))!!)

	compileOnly("maven.modrinth:world-host:0.5.0+1.21.3-fabric")
	//implementation("org.quiltmc.parsers:json:0.3.0")
	//implementation("org.semver4j:semver4j:5.3.0")

	val noxesiumVersion = "2.5.0"
	compileOnly("maven.modrinth:noxesium:$noxesiumVersion")
	//implementation("com.noxcrew.noxesium:api:$noxesiumVersion")
	//localRuntime("org.khelekore:prtree:1.5")

	compileOnly("maven.modrinth:e4mc:6.0.6-fabric")

	implementation("net.hypixel:mod-api:1.0.1")
	include(implementation("maven.modrinth:hypixel-mod-api:1.0.1+build.1+mc1.21")!!)
}

tasks.processResources {
	inputs.property("version", version)

	filesMatching("fabric.mod.json") {
		expand("version" to version)
	}
}

tasks.withType(JavaCompile::class).configureEach {
	options.encoding = "UTF-8"

	if (JavaVersion.current().isCompatibleWith(JavaVersion.VERSION_26)) {
		options.release = 25
	}
}

java {
	sourceCompatibility = JavaVersion.VERSION_25
	targetCompatibility = JavaVersion.VERSION_25
}

tasks.runClient {
	classpath(sourceSets.getByName("test").runtimeClasspath)
	jvmArgs("-XX:+AllowEnhancedClassRedefinition", "-XX:+IgnoreUnrecognizedVMOptions")
}

// Configure the maven publication
publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = base.archivesName.get()
			from(components["java"])
		}
	}

	repositories {
		maven {
			name = "owlMaven"
			val repository = if (project.version.toString().contains("beta") || project.version.toString().contains("alpha")) "snapshots" else "releases"
			url = uri("https://maven.axolotlclient.com/$repository")
			credentials(PasswordCredentials::class)
			authentication {
				create<BasicAuthentication>("basic")
			}
		}
	}
}

tasks.modrinth {
	dependsOn(tasks.getByName("optimizeOutputsOfJar"))
}

modrinth {
	token = System.getenv("MODRINTH_TOKEN")
	projectId = "p2rxzX0q"
	versionNumber = "${project.version}"
	versionType = "release"
	uploadFile = tasks.jar.get()
	gameVersions.set(listOf(minecraft))
	loaders.set(listOf("quilt", "fabric"))
	additionalFiles.set(listOf(tasks.sourcesJar))
	dependencies {
		required.project("fabric-api")
	}

	// Changelog fetching: Credit LambdAurora.
	// https://github.com/LambdAurora/LambDynamicLights/blob/1ef85f486084873b5d97b8a08df72f57859a3295/build.gradle#L145
	// License: MIT
	val changelogText = file("../CHANGELOG.md").readText()
	val regexVersion =
		((project.version) as String).split("+")[0].replace("\\.".toRegex(), "\\.").replace("\\+".toRegex(), "+")
	val changelogRegex = "###? ${regexVersion}\\n\\n(( *- .+\\n)+)".toRegex()
	val matcher = changelogRegex.find(changelogText)

	if (matcher != null) {
		var changelogContent = matcher.groups[1]?.value

		val changelogLines = changelogText.split("\n")
		val linkRefRegex = "^\\[([A-z0-9 _\\-/+.]+)]: ".toRegex()
		for (line in changelogLines.reversed()) {
			if ((linkRefRegex.matches(line)))
				changelogContent += "\n" + line
			else break
		}
		changelog = changelogContent
	} else {
		afterEvaluate {
			tasks.modrinth.configure {enabled = false}
		}
	}
}
