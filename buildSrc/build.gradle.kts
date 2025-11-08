plugins {
    kotlin("jvm") version "2.2.10"
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation("com.google.code.gson:gson:2.13.2")
    implementation("com.squareup.okhttp3:okhttp:5.2.1")
}

gradlePlugin {
    plugins {
        create("uploader") {
            id = "me.coderfrish.uploader"
            implementationClass = "me.coderfrish.uploader.UploaderPlugin"
        }
    }
}
