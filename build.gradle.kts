plugins {
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.spring) apply false
    alias(libs.plugins.kotlin.jpa) apply false
    alias(libs.plugins.spring.boot) apply false
    alias(libs.plugins.spring.dependency.management) apply false
    alias(libs.plugins.ktlint) apply false
}

sonar {
    properties {
        property("sonar.projectKey", "LiamKim-DaeYong_oort")
        property("sonar.organization", "liamkim-daeyong")
    }
}

subprojects {
    group = "io.oort"
    version = "0.0.1-SNAPSHOT"

    dependencyLocking {
        lockAllConfigurations()
    }
}
