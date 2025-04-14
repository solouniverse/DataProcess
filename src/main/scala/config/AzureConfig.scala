package com.dataprocess.engine.config

import com.typesafe.config.ConfigFactory
import org.apache.spark.sql.SparkSession

case class AzureConfig(
  storageAccount: String,
  container: String,
  clientId: String,
  tenantId: String,
  clientSecret: String
)

object AzureConfig {
  def apply(): AzureConfig = {
    val config = ConfigFactory.load()
    AzureConfig(
      storageAccount = config.getString("azure.storageAccount"),
      container = config.getString("azure.container"),
      clientId = config.getString("azure.clientId"),
      tenantId = config.getString("azure.tenantId"),
      clientSecret = config.getString("azure.clientSecret")
    )
  }

  def configureSparkSession(spark: SparkSession, azureConfig: AzureConfig): SparkSession = {
    spark.conf.set(s"fs.azure.account.auth.type.${azureConfig.storageAccount}.dfs.core.windows.net", "OAuth")
    spark.conf.set(s"fs.azure.account.oauth.provider.type.${azureConfig.storageAccount}.dfs.core.windows.net", "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
    spark.conf.set(s"fs.azure.account.oauth2.client.id.${azureConfig.storageAccount}.dfs.core.windows.net", azureConfig.clientId)
    spark.conf.set(s"fs.azure.account.oauth2.client.secret.${azureConfig.storageAccount}.dfs.core.windows.net", azureConfig.clientSecret)
    spark.conf.set(s"fs.azure.account.oauth2.client.endpoint.${azureConfig.storageAccount}.dfs.core.windows.net", s"https://login.microsoftonline.com/${azureConfig.tenantId}/oauth2/token")
    
    spark
  }
} 
