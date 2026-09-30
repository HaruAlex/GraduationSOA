name := """StudentService"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.18"

libraryDependencies ++= Seq(
  guice,
  javaJdbc,
  "com.microsoft.sqlserver" % "mssql-jdbc" % "12.6.1.jre11",
  "org.mockito" % "mockito-core" % "4.11.0" % Test
)