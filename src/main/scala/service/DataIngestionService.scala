package com.dataprocess.engine.service

import com.dataprocess.engine.config.AzureConfig
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

class DataIngestionService(spark: SparkSession, azureConfig: AzureConfig) {
  
  private val adlsPath = s"abfss://${azureConfig.container}@${azureConfig.storageAccount}.dfs.core.windows.net"
  
  def ingestCSVFiles(localPath: String): Unit = {
    val df = spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(s"$localPath/*.csv")
    
    // Write raw data
    writeToADLS(df, "raw")
    
    // Write in other formats
    writeToADLS(df, "parquet")
  }
  
  private def writeToADLS(df: DataFrame, format: String): Unit = {
    val timestamp = current_timestamp()
    val outputPath = s"$adlsPath/$format/${timestamp}"
    
    format match {
      case "raw" => 
        df.write
          .mode("overwrite")
          .option("header", "true")
          .csv(outputPath)
      
      case "parquet" =>
        df.write
          .mode("overwrite")
          .parquet(outputPath)
          
      case _ => throw new IllegalArgumentException(s"Unsupported format: $format")
    }
  }
}

object DataIngestionService {
  def apply(spark: SparkSession, azureConfig: AzureConfig): DataIngestionService = {
    new DataIngestionService(spark, azureConfig)
  }
} 