pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.googlecode.com/svn/trunk/ksoap2-android/m2-repo/") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://maven.googlecode.com/svn/trunk/ksoap2-android/m2-repo/") }
    }
}

rootProject.name = "02. CLIMOV"
include(":app")
