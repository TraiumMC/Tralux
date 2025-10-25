import io.papermc.paperweight.tasks.RebuildGitPatches
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("io.papermc.paperweight.patcher") version "2.0.0-beta.18"
}

subprojects {
    apply(plugin = "java")

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
    tasks.withType<JavaCompile>().configureEach  {
        options.encoding = Charsets.UTF_8.name()
        options.release = 21
        options.isFork = true
    }
    tasks.withType<Javadoc>().configureEach  {
        options.encoding = Charsets.UTF_8.name()
    }
    tasks.withType<ProcessResources>().configureEach  {
        filteringCharset = Charsets.UTF_8.name()
    }
    tasks.withType<Test>().configureEach  {
        testLogging {
            showStackTraces = true
            exceptionFormat = TestExceptionFormat.FULL
            events(TestLogEvent.STANDARD_OUT)
        }
    }
}

allprojects {
    tasks.withType<RebuildGitPatches>().configureEach {
        filterPatches = false
    }
}

paperweight {
    upstreams.paper {
        ref = providers.gradleProperty("paper")

        patchFile {
            path = "paper-server/build.gradle.kts"
            outputFile = file("tralux-server/build.gradle.kts")
            patchFile = file("tralux-server/build.gradle.kts.patch")
        }

        patchFile {
            path = "paper-api/build.gradle.kts"
            outputFile = file("tralux-api/build.gradle.kts")
            patchFile = file("tralux-api/build.gradle.kts.patch")
        }

        patchDir("paperApi") {
            upstreamPath = "paper-api"
            excludes = setOf("build.gradle.kts")
            patchesDir = file("tralux-api/paper-patches")
            outputDir = file("paper-api")
        }
    }
}
