package com.dataprocess.engine

import java.nio.file.{Paths, Files}
import com.dataprocess.engine.Utils
import org.apache.spark.sql.SparkSession
import com.dataprocess.engine.TransformationDataHandler


object Main {
  def main(args: Array[String]): Unit = {
    println("Go Big Or Go Home")

    val spark = SparkSession.builder().appName("GoBigOrGoHome").master("local[*]")
      .config("spark.driver.bindAddress", "127.0.0.1")
      .getOrCreate()


    //    TransformationDataHandler.processLocal(spark)
//        TransformationDataHandler.processExternal(spark)
    spark.stop()
  }
}
