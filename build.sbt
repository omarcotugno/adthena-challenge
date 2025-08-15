// at the top (optional, only if you later decide to tweak layering)
// import sbt.ClassLoaderLayeringStrategy

lazy val root = (project in file("."))
  .settings(
    name := "adthena-challenge",
    ThisBuild / scalaVersion := "2.12.18",
    Compile / scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint"),

    // ✅ Run the app in a separate JVM to avoid SBT's layered classloader
    Compile / run / fork := true,

    // ✅ JDK 17 module flags Spark needs
    Compile / run / javaOptions ++= Seq(
      "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
      "--add-opens=java.base/java.nio=ALL-UNNAMED"
    ),

    Test / fork := true,
    Test / parallelExecution := false,

    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-sql" % "3.5.1",
      "org.scalatest"    %% "scalatest" % "3.2.19" % Test
    )
  )
