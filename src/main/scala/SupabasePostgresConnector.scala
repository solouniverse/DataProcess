package com.dataprocess.engine

import org.apache.spark.sql.{SparkSession, DataFrame}
import java.util.Properties

object SupabasePostgresConnector {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder()
      .appName("Supabase PostgreSQL Connection")
      .master("local[*]")
      .getOrCreate()

    // JDBC connection parameters
    val host = "aws-0-us-west-1.pooler.supabase.com"
    val port = "6543"
    val database = "postgres"
    val user = "postgres.keneuducruuemmrhdmcy"
    val password = "P@s$wOrd@01" //"P%40s%24wOrd%4001"  // 🔐 Replace with actual password
    val poolMode = "transaction"      // Not a standard JDBC param, included just for completeness

    // Construct JDBC URL
    //jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres?user=postgres.keneuducruuemmrhdmcy&password=[YOUR-PASSWORD]
    val jdbcUrl = s"jdbc:postgresql://$host:$port/$database"

    // Set connection properties
    val connectionProperties = new Properties()
    connectionProperties.setProperty("user", user)
    connectionProperties.setProperty("password", password)
    connectionProperties.setProperty("sslmode", "require") // Supabase requires SSL

    try {
      println("📡 Connecting to Supabase PostgreSQL...")

      // Example query: list all tables in public schema
      val query = "SELECT table_name FROM information_schema.tables WHERE table_schema = 'public'"

      val tablesDF: DataFrame = spark.read
        .jdbc(jdbcUrl, query, connectionProperties)

      tablesDF.show()

    } catch {
      case ex: Exception =>
        println(s"❌ Connection failed: ${ex.getMessage}")
    } finally {
      spark.stop()
    }
  }
}
