import scala.sys.process._

ThisBuild / scalaVersion := "2.12.18"

lazy val sparkVersion = "3.5.1"

ThisBuild / organization := "com.example"
ThisBuild / version      := "0.1.0"

lazy val root = (project in file(".")).settings(
  name        := "pricebasket",
  libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-sql" % sparkVersion % Provided,
    "org.scalatest"    %% "scalatest" % "3.2.18"     % Test,
    "org.apache.spark" %% "spark-sql" % sparkVersion % Test,
    "com.typesafe"      % "config"    % "1.4.3"
  ),
  Test / fork := true,
  Test / javaOptions ++= Seq(
    "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
    "--add-opens=java.base/java.nio=ALL-UNNAMED",
    "-Xmx1G",
    "-Dspark.master=local[2]",
    "-Duser.timezone=UTC"
  )
)

lazy val runSpark = inputKey[Unit]("Run the app via spark-submit (keeps Spark % Provided)")

Compile / run / fork := true
Compile / run / javaOptions ++= Seq(
  "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED",
  "--add-opens=java.base/java.nio=ALL-UNNAMED",
  "-Xmx1G",
  "-Dspark.master=local[*]",
  "-Duser.timezone=UTC"
)

runSpark := {
  val args = Def.spaceDelimited().parsed
  val log  = streams.value.log
  val jar  = (Compile / packageBin).value

  val sparkHome   = sys.env.get("SPARK_HOME")
  val sparkSubmit = sparkHome match {
    case Some(home) => s"$home/bin/spark-submit"
    case None       => "spark-submit" // rely on PATH
  }

  val master     = sys.props.getOrElse("spark.master", "local[*]")
  val driverOpts =
    "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED --add-opens=java.base/java.nio=ALL-UNNAMED -Duser.timezone=UTC"

  val cmd = Seq(
    sparkSubmit,
    "--class",
    "com.example.pricebasket.app.PriceBasketApp",
    "--master",
    master,
    "--conf",
    s"spark.driver.extraJavaOptions=$driverOpts",
    "--packages",
    "com.typesafe:config:1.4.3",
    jar.getAbsolutePath
  ) ++ args

  log.info(s"Running: ${cmd.mkString(" ")}")
  val exit = Process(cmd).!
  if (exit != 0) sys.error(s"spark-submit failed with exit code $exit")
}
