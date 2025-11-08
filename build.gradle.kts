import StandardConstants.CREATE_MOJMAP_PAPERCLIP_TASK
import StandardConstants.GITHUB_BASE_LINK
import StandardConstants.SERVER_PROJECT_NAME
import StandardConstants.SHIELDS_DOWNLOADS_BASE_LINK
import io.papermc.paperweight.tasks.RebuildGitPatches
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import java.nio.file.Files
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

plugins {
    id("io.papermc.paperweight.patcher") version "2.0.0-beta.18"
    id("org.ajoberstar.grgit") version "5.0.0"
    id("me.coderfrish.uploader")
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }


    dependencies {
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }

    tasks.withType<JavaCompile>().configureEach  {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
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
        filterPatches.set(false)
    }
}

tasks.register("createTraluxPaperclipJar") {
    group = "bundling"

    val buildDirectory = buildDirectoryLayout(SERVER_PROJECT_NAME)
    dependsOn(project(":$SERVER_PROJECT_NAME").tasks.getByName(
        CREATE_MOJMAP_PAPERCLIP_TASK
    ))

    val rawJar = mojmapPaperclipJarFile(buildDirectory)
    doLast {
        val source = rawJar.get().asFile.toPath()
        val target = traluxPaperclipJarFile(
            project.layout.buildDirectory
        ).get().asFile.toPath()

        /* move file... */
        Files.move(source, target)
    }
}

/* publish time */
val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
val time: String = "${LocalDateTime.now().format(formatter)} UTC"

/* minecraft version */
val mcVersion = providers.gradleProperty("mcVersion").get()

val gitBranchName: String = grgit.branch.current().name
val gitCommentFullHash: String by extra(grgit.head().id)
val gitCommentHash: String by extra(grgit.head().abbreviatedId)
val gitCommentMessage: String by extra(grgit.head().shortMessage)
uploader {
    token = System.getenv("GITHUB_TOKEN")

    repository {
        owner = "TraiumMC"
        name = "Tralux"
    }

    targetCommitish = gitBranchName
    name = "Tralux $mcVersion"
    tagName = "$mcVersion-$gitCommentHash"

    prorelease = providers.gradleProperty("prerelease").get().toBoolean()

    val target = traluxPaperclipJarFile(project.layout.buildDirectory)
    assets.add(target.get().asFile)

    body = """
    ### 📦 Version: `$mcVersion` | Commit: [$gitCommentHash]($GITHUB_BASE_LINK/commit/$gitCommentFullHash)
    ![download]($SHIELDS_DOWNLOADS_BASE_LINK/$mcVersion-$gitCommentHash/total?style=for-the-badge)

    > This release is automatically built by GitHub Actions.

    #### 📜 Latest Commit Message:
    > $gitCommentMessage

    #### 📊 Build Information:
    - **Build Status**: ✅ Success
    - **Build Date**: $time
    """.trimIndent()
}

paperweight {
    upstreams.paper {
        ref.set(providers.gradleProperty("paper"))

        patchFile {
            path.set("paper-server/build.gradle.kts")
            outputFile.set(file("tralux-server/build.gradle.kts"))
            patchFile.set(file("tralux-server/build.gradle.kts.patch"))
        }

        patchFile {
            path.set("paper-api/build.gradle.kts")
            outputFile.set(file("tralux-api/build.gradle.kts"))
            patchFile.set(file("tralux-api/build.gradle.kts.patch"))
        }

        patchDir("paperApi") {
            upstreamPath.set("paper-api")
            excludes.set(setOf("build.gradle.kts"))
            patchesDir.set(file("tralux-api/paper-patches"))
            outputDir.set(file("paper-api"))
        }
    }
}

fun buildDirectoryLayout(projectName: String): DirectoryProperty {
    return project(":$projectName").layout.buildDirectory
}

fun traluxPaperclipJarFile(directoryLayout: DirectoryProperty): Provider<RegularFile> {
    val libsDirectory = directoryLayout.file("libs")
    if (!libsDirectory.get().asFile.exists()) {
        libsDirectory.get().asFile.mkdirs()
    }

    return directoryLayout.file("libs/$name-$version.jar")
}

fun mojmapPaperclipJarFile(directoryLayout: DirectoryProperty): Provider<RegularFile> {
    return directoryLayout.file("libs/$name-paperclip-$version-mojmap.jar")
}

object StandardConstants {
    const val CREATE_MOJMAP_PAPERCLIP_TASK = "createMojmapPaperclipJar"

    const val SERVER_PROJECT_NAME = "tralux-server"

    const val REPOSITORY_NAME = "TraiumMC/Tralux"

    const val GITHUB_BASE_LINK = "https://github.com/$REPOSITORY_NAME"

    const val SHIELDS_BASE_LINK = "https://img.shields.io"

    const val SHIELDS_DOWNLOADS_BASE_LINK = "$SHIELDS_BASE_LINK/github/downloads/$REPOSITORY_NAME"
}
