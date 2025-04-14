package com.dataprocess.engine.processor

import com.dataprocess.engine.config.{AppConfig, AzureConfig, SparkConfig}
import com.dataprocess.engine.utils.ADLSConnection
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class DataProcessorTest extends AnyFunSuite with Matchers with BeforeAndAfterAll {
  private var spark: SparkSession = _
  private var processor: DataProcessor = _
  private var testConfig: AppConfig = _

  override def beforeAll(): Unit = {
    // Create test Spark session
    spark = SparkSession.builder()
      .appName("DataProcessorTest")
      .master("local[2]")
      .config("spark.driver.memory", "2g")
      .getOrCreate()

    // Create test configuration
    testConfig = new AppConfig {
      override val azureConfig: AzureConfig = AzureConfig(
        storageAccount = "test-account",
        container = "test-container",
        clientId = "test-client-id",
        tenantId = "test-tenant-id",
        clientSecret = "test-client-secret"
      )

      override val sparkConfig: SparkConfig = SparkConfig(
        master = "local[2]",
        appName = "test-app",
        executorMemory = "2g",
        driverMemory = "2g"
      )

      override val inputPath: String = "src/test/resources/test-data"
      override val outputPath: String = "src/test/resources/output"
    }
  }

  override def afterAll(): Unit = {
    spark.stop()
  }

  test("DataProcessor should read CSV files correctly") {
    val adlsConnection = new ADLSConnection(testConfig.azureConfig)
    processor = new DataProcessor(spark, testConfig, adlsConnection)

    val df = processor.readCSVFiles()
    df should not be null
    df.count() should be > 0L
  }

  test("DataProcessor should apply transformations correctly") {
    val adlsConnection = new ADLSConnection(testConfig.azureConfig)
    processor = new DataProcessor(spark, testConfig, adlsConnection)

    val testDF = spark.createDataFrame(Seq(
      (1, "test1"),
      (2, "test2")
    )).toDF("id", "name")

    val transformedDF = processor.applyTransformations(testDF)
    transformedDF should not be null
    transformedDF.count() shouldBe testDF.count()
  }
}