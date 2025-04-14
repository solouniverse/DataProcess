package com.dataprocess.engine.utils

import com.dataprocess.engine.config.AzureConfig
import org.apache.spark.sql.SparkSession
import org.apache.hadoop.fs.azurebfs.AzureBlobFileSystem
import org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider

class ADLSConnection(config: AzureConfig) {
  private val accountName = config.storageAccount
  private val container = config.container
  private val clientId = config.clientId
  private val tenantId = config.tenantId
  private val clientSecret = config.clientSecret

  def configureSparkSession(spark: SparkSession): SparkSession = {
    val abfsEndpoint = s"abfss://$container@$accountName.dfs.core.windows.net"

    spark.conf.set(s"fs.azure.account.auth.type.$accountName.dfs.core.windows.net", "OAuth")
    spark.conf.set(s"fs.azure.account.oauth.provider.type.$accountName.dfs.core.windows.net", "org.apache.hadoop.fs.azurebfs.oauth2.ClientCredsTokenProvider")
    spark.conf.set(s"fs.azure.account.oauth2.client.id.$accountName.dfs.core.windows.net", clientId)
    spark.conf.set(s"fs.azure.account.oauth2.client.secret.$accountName.dfs.core.windows.net", clientSecret)
    spark.conf.set(s"fs.azure.account.oauth2.client.endpoint.$accountName.dfs.core.windows.net", s"https://login.microsoftonline.com/$tenantId/oauth2/token")

    spark
  }

  def getADLSPath(path: String): String = {
    s"abfss://$container@$accountName.dfs.core.windows.net/$path"
  }
}