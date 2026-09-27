name := """ThesisService"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.18"

libraryDependencies += guice
libraryDependencies += "com.microsoft.sqlserver" % "mssql-jdbc" % "12.8.1.jre11"
