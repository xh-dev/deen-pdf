
name := "deen-pdf"
description := "deen pdf is scala based test, decrypt and encrypt pdf tool"

version := "0.1"

scalaVersion := "2.13.5"

libraryDependencies += "org.apache.pdfbox" % "pdfbox" % "2.0.23"

libraryDependencies += "com.github.scopt" %% "scopt" % "4.0.1"

assemblyJarName in assembly := "ende-pdf.jar"
