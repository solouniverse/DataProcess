package com.dataprocess.engine

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.{DataFrame, SparkSession}
import java.util.Properties
import java.nio.file.{Files, Paths}
import org.apache.log4j.{Logger, LogManager}

object TransformationDataHandler {

  val logger: Logger = LogManager.getLogger(getClass.getName)

  def processExternal(spark: SparkSession): Unit = {
    logger.info("🔄 Starting external data processing...")

    // ✅ Correct JDBC URL for Supabase (with sslmode=require)
    val jdbcUrl = "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres?user=postgres.keneuducruuemmrhdmcy&password=P%40s%24wOrd%4001&sslmode=require" //?sslmode=require
//    jdbc:postgresql://db.keneuducruuemmrhdmcy.supabase.co:5432/postgres?user=postgres&password=[YOUR-PASSWORD]/
//    jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres?user=postgres.keneuducruuemmrhdmcy&password=[YOUR-PASSWORD]
//    jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:5432/postgres?user=postgres.keneuducruuemmrhdmcy&password=[YOUR-PASSWORD]
    // ✅ Use the full username from Supabase connection info
    val props = new java.util.Properties()
    props.setProperty("user", "postgres")
    props.setProperty("password", "P%40s%24wOrd%4001")

    try {
      logger.info("📡 Connecting to PostgreSQL...")

      // 🔍 Step 1: Fetch all table names from 'public' schema
      val tablesDF: DataFrame = spark.read
        .jdbc(
          jdbcUrl,
          "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';",
          props
        )

      val tableNames = tablesDF.collect().map(_.getString(0))
      logger.info(s"✅ Found ${tableNames.length} tables: ${tableNames.mkString(", ")}")

      // 📥 Step 2: Load and display each table
      tableNames.foreach { tableName =>
        try {
          logger.info(s"📥 Reading table: $tableName")
          val df = spark.read
            .jdbc(jdbcUrl, s"public.$tableName", props)

          df.show(20, truncate = false)
          df.printSchema()
        } catch {
          case e: Exception =>
            logger.error(s"❌ Failed to load table: $tableName", e)
        }
      }

    } catch {
      case e: Exception =>
        logger.error("❌ Failed to connect to Supabase or read tables", e)
    } finally {
      logger.info("🛑 Spark session completed.")
    }
  }

  def processLocal(spark: SparkSession): Unit = {
    logger.info("🔄 Starting local data processing...")

    val projectRoot = Paths.get(System.getProperty("user.dir"))
    val filePath = projectRoot.resolve("data/SupplyChain.csv")

    try {
      if (Files.exists(filePath)) {
        logger.info(s"✅ File found: ${filePath.toAbsolutePath}")
        val df = spark.read
          .options(Map("delimiter" -> ",", "header" -> "true"))
          .csv(filePath.toString)
        df.show(20, truncate = false)
        df.printSchema()
      } else {
        logger.warn(s"⚠️ File not found: ${filePath.toAbsolutePath}")
      }
    } catch {
      case e: Exception =>
        logger.error("❌ Error occurred while processing local file", e)
    }
  }
}
