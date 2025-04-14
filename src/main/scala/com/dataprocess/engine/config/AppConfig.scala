package com.dataprocess.engine.config

import com.typesafe.config.{Config, ConfigFactory}

case class AzureConfig(
  storageAccount: String,
  container: String,
  clientId: String,
  tenantId: String,
  clientSecret: String
)

case class SparkConfig(
  master: String,
  appName: String,
  executorMemory: String,
  driverMemory: String
)

class AppConfig {
  private val config: Config = ConfigFactory.load()

  val azureConfig: AzureConfig = AzureConfig(
    storageAccount = config.getString("azure.storageAccount"),
    container = config.getString("azure.container"),
    clientId = config.getString("azure.clientId"),
    tenantId = config.getString("azure.tenantId"),
    clientSecret = config.getString("azure.clientSecret")
  )

  val sparkConfig: SparkConfig = SparkConfig(
    master = config.getString("spark.master"),
    appName = config.getString("spark.appName"),
    executorMemory = config.getString("spark.executorMemory"),
    driverMemory = config.getString("spark.driverMemory")
  )

  val inputPath: String = config.getString("paths.input")
  val outputPath: String = config.getString("paths.output")
}