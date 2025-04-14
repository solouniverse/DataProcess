ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

lazy val root = (project in file("."))
  .settings(
    name := "DataProcess"
    //,idePackagePrefix := Some("com.dataprocess.engine")
  )

//  % "Provided" / % Runtime /  % Test append one of these appropriately
val sparkVersion = "3.5.4"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.postgresql" % "postgresql" % "42.2.5",
  "log4j" % "log4j" % "1.2.17",
  "com.azure" % "azure-identity" % "1.11.2",
  "com.azure" % "azure-security-keyvault-secrets" % "4.7.2",
  "org.apache.hadoop" % "hadoop-azure" % "3.3.6",
  "com.azure" % "azure-storage-blob" % "12.25.0",
  "org.scalatest" %% "scalatest" % "3.2.17" % Test,
  "org.mockito" %% "mockito-scala" % "1.17.30" % Test,
  "com.typesafe" % "config" % "1.4.2"
)
