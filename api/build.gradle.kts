plugins {
    id("java-library")
    id("maven-publish")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly(files("../libs/EdTools-API.jar"))
    compileOnly(files("../libs/Rival-FRods.jar"))
    compileOnly(files("../libs/Rival-Hoes.jar"))
    compileOnly(files("../libs/Rival-Pickaxes.jar"))
    compileOnly(files("../libs/Rival-Swords.jar"))
}


java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            groupId = group.toString()
            version = project.version.toString()
            artifactId = "sh-pets-api"
            version = project.version.toString()
        }
    }
}