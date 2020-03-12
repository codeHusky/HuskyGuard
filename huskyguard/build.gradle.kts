import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.internal.HasConvention

plugins {
    id("java-library")
    id("net.ltgt.apt-eclipse")
    id("net.ltgt.apt-idea")
    id("org.spongepowered.plugin") version "0.9.0"
}

applyPlatformAndCoreConfiguration()
applyShadowConfiguration()

repositories {
    maven("https://repo.minecrell.net/releases/")
}

dependencies {
    "compile"(project(":worldguard-core"))
    "api"("com.sk89q.worldedit:worldedit-bukkit:${Versions.WORLDEDIT}") { isTransitive = false }
    "implementation"("com.sk89q:commandbook:2.3") { isTransitive = false }
    "compile"("org.spongepowered:spongeapi:7.1.0")
}

tasks.named<Upload>("install") {
    (repositories as HasConvention).convention.getPlugin<MavenRepositoryHandlerConvention>().mavenInstaller {
        pom.whenConfigured {
            dependencies.firstOrNull { dep ->
                dep!!.withGroovyBuilder {
                    getProperty("groupId") == "com.destroystokyo.paper" && getProperty("artifactId") == "paper-api"
                }
            }?.withGroovyBuilder {
                setProperty("groupId", "org.bukkit")
                setProperty("artifactId", "bukkit")
            }
        }
    }
}

tasks.named<Copy>("processResources") {

}

tasks.named<Jar>("jar") {
    manifest {
        attributes("Implementation-Version" to project.version)
    }
}

tasks.named<ShadowJar>("shadowJar") {
    dependencies {
        /*relocate("org.bstats", "com.sk89q.worldguard.bukkit.bstats") {
            include(dependency("org.bstats:bstats-bukkit:1.7"))
        }
        relocate ("io.papermc.lib", "com.sk89q.worldguard.bukkit.paperlib") {
            include(dependency("io.papermc:paperlib:1.0.2"))
        }*/
    }
}

tasks.named("assemble").configure {
    dependsOn("shadowJar")
}