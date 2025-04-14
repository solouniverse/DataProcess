package com.dataprocess.engine

import com.dataprocess.engine.config.AppConfig
import com.dataprocess.engine.processor.DataProcessor
import com.dataprocess.engine.utils.ADLSConnection
import org.apache.spark.sql.SparkSession
import scopt.OParser

case class Config(
  mode: String = "local",
  inputPath: String = "",
  outputPath: String = "",
  useCloud: Boolean = false
)

object DataProcessApp {
  def main(args: Array[String]): Unit = {
    val builder = OParser.builder[Config]
    val parser = {
      import builder._
      OParser.sequence(
        programName("DataProcessApp"),
        head("DataProcessApp", "1.0"),
        opt[String]('m', "mode")
          .action((x, c) => c.copy(mode = x))
          .text("Processing mode: local or cloud"),
        opt[String]('i', "input")
          .action((x, c) => c.copy(inputPath = x))
          .text("Input path for data files"),
        opt[String]('o', "output")
          .action((x, c) => c.copy(outputPath = x))
          .text("Output path for processed files"),
        opt[Boolean]('c', "cloud")
          .action((x, c) => c.copy(useCloud = x))
          .text("Use cloud storage (ADLS Gen2)")
      )
    }

    OParser.parse(parser, args, Config()) match {
      case Some(config) =>
        processData(config)
      case _ =>
        println("Invalid arguments. Use --help for usage information.")
        sys.exit(1)
    }
  }

  private def processData(config: Config): Unit = {
    // Initialize configuration
    val appConfig = new AppConfig

    // Create Spark session
    val spark = SparkSession.builder()
      .appName(appConfig.sparkConfig.appName)
      .master(appConfig.sparkConfig.master)
      .config("spark.executor.memory", appConfig.sparkConfig.executorMemory)
      .config("spark.driver.memory", appConfig.sparkConfig.driverMemory)
      .getOrCreate()

    try {
      // Initialize ADLS connection if needed
      val adlsConnection = if (config.useCloud) {
        val connection = new ADLSConnection(appConfig.azureConfig)
        connection.configureSparkSession(spark)
        Some(connection)
      } else None

      // Create data processor
      val processor = new DataProcessor(spark, appConfig, adlsConnection.getOrElse(null))

      // Process data based on mode
      config.mode.toLowerCase match {
        case "local" =>
          if (config.inputPath.isEmpty || config.outputPath.isEmpty) {
            println("Local mode requires both input and output paths")
            sys.exit(1)
          }
          processor.processAndWriteLocalData(config.inputPath, config.outputPath)
          println(s"Data processed and written to local path: ${config.outputPath}")

        case "cloud" =>
          val inputPath = if (config.inputPath.nonEmpty) Some(config.inputPath) else None
          processor.processAndWriteCloudData(inputPath)
          println("Data processed and written to ADLS Gen2")

        case _ =>
          println(s"Invalid mode: ${config.mode}. Use 'local' or 'cloud'")
          sys.exit(1)
      }

    } catch {
      case e: Exception =>
        println(s"Error processing data: ${e.getMessage}")
        e.printStackTrace()
    } finally {
      spark.stop()
    }
  }
}