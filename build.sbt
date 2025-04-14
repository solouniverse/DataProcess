ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "DataProcess",
    idePackagePrefix := Some("com.dataprocess.engine")
  )

//  % "Provided" / % Runtime /  % Test append one of these appropriately
val sparkVersion = "3.5.4"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.5.4",
  "org.apache.spark" %% "spark-sql" % "3.5.4",
  "org.postgresql" % "postgresql" % "42.2.5",
  "log4j" % "log4j" % "1.2.17",
  "com.azure" % "azure-identity" % "1.11.2",
  "com.azure" % "azure-security-keyvault-secrets" % "4.7.2"
)
