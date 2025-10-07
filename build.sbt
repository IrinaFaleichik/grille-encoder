ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.6"

lazy val root = (project in file("."))
  .settings(
    name := "grille-encoder",
    idePackagePrefix := Some("irka.grille-encoder")
  )

// ZIO
libraryDependencies ++= Seq(
  "dev.zio" %% "zio" % "2.1.21",
  "dev.zio" %% "zio-streams" % "2.1.21",
  // or ZIO Modules
    "io.getquill" %% "quill-jdbc-zio" % "4.7.3",
  "io.getquill" %% "quill-jdbc" % "4.7.3"
)
// ZIO
libraryDependencies ++= Seq(
  "dev.zio" %% "zio-config"          % "4.0.5",
  "dev.zio" %% "zio-config-magnolia" % "4.0.5",
  "dev.zio" %% "zio-config-typesafe" % "4.0.5",
  "dev.zio" %% "zio-config-refined"  % "4.0.5",
)

// DB
libraryDependencies ++= Seq(
  "mysql" % "mysql-connector-java" % "8.0.33",
  "org.xerial" % "sqlite-jdbc" % "3.50.3.0" //
  // Syncronous JDBC Modules
  //  "io.getquill" %% "quill-jdbc" % "4.7.3",
)


//libraryDependencies += "dev.zio" %% "zio-jdbc" % "0.1.2" // Replace with the latest version
