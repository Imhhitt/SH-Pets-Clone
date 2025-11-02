plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
}

dependencies {
    implementation(project(":api"))
    paperweight.paperDevBundle("1.21.5-R0.1-SNAPSHOT")

    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.12")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.12")

    implementation("dev.dejvokep:boosted-yaml:1.3.6")
    implementation("com.h2database:h2:2.1.214")
    implementation("dev.triumphteam:triumph-gui:3.1.13")
    implementation("com.saicone.rtag:rtag:1.5.11")
    implementation("com.saicone.rtag:rtag-item:1.5.11")
    implementation("dev.rollczi:liteskullapi:1.3.0")

    compileOnly("net.kyori:adventure-api:4.17.0")
    compileOnly("net.kyori:adventure-platform-bukkit:4.4.1")
    compileOnly("net.kyori:adventure-text-minimessage:4.17.0")
    compileOnly("me.clip:placeholderapi:2.11.6")


    compileOnly(files("../libs/EdTools-API.jar"))
}

tasks {
    shadowJar {
        archiveFileName.set("SH-Pets-${project.version}.jar")
        archiveClassifier.set("")

        from(project(":plugin").sourceSets.main.get().output)
        from(project(":api").sourceSets.main.get().output)

        relocate("dev.triumphteam.gui", "dev.smartshub.shpets.libs.gui")
        relocate("org.h2", "dev.smartshub.shpets.libs.h2")
        relocate("revxrsal.commands", "dev.smartshub.shpets.libs.lamp")
        relocate("com.saicone.rtag", "dev.smartshub.shpets.libs.rtag")
        relocate("dev.dejvokep.boostedyaml", "dev.smartshub.shpets.libs.boostedyaml")

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        exclude("**/org/jetbrains/**")
        exclude("**/org/intellij/**")
        exclude("META-INF/MANIFEST.MF")
        exclude("**/*.kotlin_metadata")
        exclude("**/*.kotlin_module")
        exclude("**/*.SF")
        exclude("**/*.DSA")
        exclude("**/*.RSA")
    }
    processResources {
        filesMatching("plugin.yml") {
            expand("version" to project.version)
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        }
    }
    build {
        dependsOn(shadowJar)
    }
}

