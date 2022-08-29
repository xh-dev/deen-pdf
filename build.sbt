name := "deen-pdf"
description := "deen pdf is scala based test, decrypt and encrypt pdf tool"

version := "0.1"

scalaVersion := "2.13.6"

libraryDependencies += "org.apache.pdfbox" % "pdfbox" % "2.0.26"

libraryDependencies += "com.github.scopt" %% "scopt" % "4.1.0"

assemblyJarName in assembly := "deen-pdf.jar"
