ThisBuild / scalaVersion := "3.2.0"
cancelable in Global := true
run / fork := true
lazy val socl = (project in file("."))
  .settings(
    name := "socl",
    Compile / scalaSource := baseDirectory.value / "src",

    libraryDependencies += "org.jocl" % "jocl" % "2.0.4"

  )
