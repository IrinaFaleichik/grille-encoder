ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "grilleEncoder",
    idePackagePrefix := Some("irka.grilleEncoder")
  )

// ZIO
libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.1.21",
  "dev.zio" %% "zio-streams" % "2.1.21",
)
libraryDependencies ++= Seq(
  "dev.zio" %% "zio-http" % "3.3.3"
  // DB ZIO Modules
)

libraryDependencies ++= Seq(
  "dev.zio" %% "zio-test" % "2.1.23" % Test,
  "dev.zio" %% "zio-test-sbt" % "2.1.23" % Test,
  "dev.zio" %% "zio-http-testkit" % "3.3.3" % Test
)
testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")

// ZIO config
libraryDependencies ++= Seq(
  "dev.zio" %% "zio-config" % "4.0.5",
  "dev.zio" %% "zio-config-magnolia" % "4.0.5",
  "dev.zio" %% "zio-config-typesafe" % "4.0.5",
  "dev.zio" %% "zio-config-refined" % "4.0.5",
)

// DB
libraryDependencies ++= Seq(
  //  "mysql" % "mysql-connector-java" % "8.0.33",
  //  "org.flywaydb" % "flyway-core" % "10.12.0",
  "org.xerial" % "sqlite-jdbc" % "3.50.3.0", //% "SQLITE_OMIT_FOREIGN_KEY", //SQLITE_OMIT_FOREIGN_KEY
  "io.getquill" %% "quill-jdbc-zio" % "4.7.3",
  "io.getquill" %% "quill-jdbc" % "4.7.3"
  // Syncronous JDBC Modules
  //  "io.getquill" %% "quill-jdbc" % "4.7.3",
)
// Force zio-json to a single version
dependencyOverrides += "dev.zio" %% "zio-json" % "0.6.2"


//libraryDependencies += "dev.zio" %% "zio-jdbc" % "0.1.2" // Replace with the latest version
