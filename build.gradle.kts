// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  alias(libs.plugins.android.application) apply false
  alias(libs.plugins.compose.compiler) apply false
  alias(libs.plugins.kotlin.serialization) apply false
  alias(libs.plugins.kotlin.jvm) apply false
  alias(ktorLibs.plugins.ktor) apply false
}

tasks.register<Exec>("dockerUp") {
  group = "docker"
  description = "Build and start local Docker containers"

  workingDir(rootProject.projectDir)

  commandLine(
    "docker",
    "compose",
    "up",
    "--build",
    "-d",
  )
}

tasks.register<Exec>("dockerDown") {
  group = "docker"
  description = "Stop local Docker containers"

  workingDir(rootProject.projectDir)

  commandLine(
    "docker",
    "compose",
    "down",
  )
}

tasks.register<Exec>("reverseLocalServer") {
  group = "development"
  description = "Reverse local server port 8080 via adb"

  commandLine(
    "adb",
    "reverse",
    "tcp:8080",
    "tcp:8080",
  )
}