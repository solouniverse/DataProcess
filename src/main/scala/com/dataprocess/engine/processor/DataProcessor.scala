package com.dataprocess.engine.processor

import com.dataprocess.engine.config.AppConfig
import com.dataprocess.engine.utils.ADLSConnection
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions._

class DataProcessor(spark: SparkSession, config: AppConfig, adlsConnection: ADLSConnection) {

  def readCSVFiles(localPath: Option[String] = None): DataFrame = {
    val inputPath = localPath.getOrElse(config.inputPath)
    spark.read
      .option("header", "true")
      .option("inferSchema", "true")
      .csv(inputPath)
  }

  def writeToLocal(df: DataFrame, format: String, path: String): Unit = {
    df.write
      .mode("overwrite")
      .format(format)
      .save(path)
  }

  def writeToADLS(df: DataFrame, format: String, path: String): Unit = {
    val outputPath = adlsConnection.getADLSPath(path)
    df.write
      .mode("overwrite")
      .format(format)
      .save(outputPath)
  }

  def processAndWriteLocalData(localInputPath: String, localOutputPath: String): Unit = {
    // Read CSV files from local
    val df = readCSVFiles(Some(localInputPath))

    // Write to different formats locally
    writeToLocal(df, "csv", s"$localOutputPath/csv")
    writeToLocal(df, "parquet", s"$localOutputPath/parquet")
    writeToLocal(df, "json", s"$localOutputPath/json")
    writeToLocal(df, "iceberg", s"$localOutputPath/iceberg")
  }

  def processAndWriteCloudData(localInputPath: Option[String] = None): Unit = {
    // Read CSV files
    val df = readCSVFiles(localInputPath)

    // Write raw data to ADLS
    writeToADLS(df, "csv", "raw/data")

    // Write as Parquet
    writeToADLS(df, "parquet", "processed/data")

    // Write as JSON
    writeToADLS(df, "json", "processed/json")

    // Write as Iceberg
    writeToADLS(df, "iceberg", "processed/iceberg")

    // Additional transformations can be added here
    val transformedDF = df.transform(applyTransformations)
    writeToADLS(transformedDF, "parquet", "transformed/data")
  }

  private def applyTransformations(df: DataFrame): DataFrame = {
    // Add your transformation logic here
    // Example: df.withColumn("new_column", some_transformation)
    df
  }
}