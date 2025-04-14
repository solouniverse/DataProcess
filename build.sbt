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
  "org.apache.spark" %% "spark-core" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-hadoop-cloud" % sparkVersion,
  "org.apache.hadoop" % "hadoop-azure" % "3.3.6",
  "org.apache.hadoop" % "hadoop-azure-datalake" % "3.3.6",
  "org.postgresql" % "postgresql" % "42.2.5",
  "log4j" % "log4j" % "1.2.17",
  "com.azure" % "azure-identity" % "1.11.2",
  "com.azure" % "azure-security-keyvault-secrets" % "4.7.2",
  "com.github.scopt" %% "scopt" % "4.1.0",
  "org.apache.iceberg" %% "iceberg-spark-runtime-3.5" % "1.4.3",
  "org.scalatest" %% "scalatest" % "3.2.17" % Test,
  "org.mockito" %% "mockito-scala" % "1.17.30" % Test,
  "org.scalatestplus" %% "mockito-4-6" % "3.2.15.0" % Test
)

// Add configuration for testing
Test / fork := true
Test / javaOptions ++= Seq(
  "-Dspark.master=local[2]",
  "-Dspark.driver.memory=2g"
)
