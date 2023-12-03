name := "threads-sbt"
version := "1.0"
scalaVersion := "2.13.6"

libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.2.9",
  "org.scalatest" %% "scalatest" % "3.2.9" % "test",
  "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.3"
)