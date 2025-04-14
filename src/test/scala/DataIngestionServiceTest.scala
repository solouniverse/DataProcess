package com.dataprocess.engine.service

import com.dataprocess.engine.config.AzureConfig
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class DataIngestionServiceTest extends AnyFunSuite with Matchers with BeforeAndAfterAll {

  private var spark: SparkSession = _
  private var azureConfig: AzureConfig = _
  private var dataIngestionService: DataIngestionService = _

  override def beforeAll(): Unit = {
    spark = SparkSession.builder()
      .appName("DataIngestionTest")
      .master("local[2]")
      .getOrCreate()

    azureConfig = AzureConfig(
      storageAccount = "testaccount",
      container = "testcontainer",
      clientId = "test-client-id",
      tenantId = "test-tenant-id",
      clientSecret = "test-client-secret"
    )

    dataIngestionService = DataIngestionService(spark, azureConfig)
  }

  override def afterAll(): Unit = {
    spark.stop()
  }

  test("should read CSV files correctly") {
    // Create a temporary CSV file
    val testData = Seq(
      ("1", "John", "Doe"),
      ("2", "Jane", "Smith")
    )

    val df = spark.createDataFrame(testData).toDF("id", "first_name", "last_name")
    val tempPath = "target/test-data"
    df.write
      .option("header", "true")
      .csv(tempPath)

    // Test the ingestion
    dataIngestionService.ingestCSVFiles(tempPath)

    // Verify the data was processed
    val result = spark.read
      .option("header", "true")
      .csv(s"$tempPath/*.csv")

    result.count() shouldBe 2
    result.columns should contain allOf("id", "first_name", "last_name")
  }

  test("should handle empty directory gracefully") {
    val emptyPath = "target/empty-data"
    new java.io.File(emptyPath).mkdirs()

    // This should not throw an exception
    dataIngestionService.ingestCSVFiles(emptyPath)
  }
}