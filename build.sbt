ThisBuild / scalaVersion := "3.2.0"
cancelable in Global := true
run / fork := true
lazy val socl = (project in file("."))
  .settings(
    name := "socl",
    Compile / scalaSource := baseDirectory.value / "src",

    libraryDependencies += "org.jocl" % "jocl" % "2.0.4",
    libraryDependencies += "org.typelevel" %% "cats-core" % "2.8.0",
    libraryDependencies += "org.typelevel" %% "cats-effect" % "3.4-148221d",
    scalacOptions += "-explain"
  )
