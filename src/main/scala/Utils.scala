package com.dataprocess.engine

object Utils {
  def printPretty(df: org.apache.spark.sql.DataFrame, numRows: Int = 20): Unit = {
    val rows = df.take(numRows)
    val headers: Seq[String] = df.columns.toSeq

    // Convert each row to Seq[String]
    val data: Seq[Seq[String]] = rows.map { row =>
      row.toSeq.map {
        case null => "null"
        case x    => x.toString
      }
    }.toSeq

    // Combine headers and data
    val allRows: Seq[Seq[String]] = headers +: data

    // Calculate column widths
    val colWidths = allRows.transpose.map(_.map(_.length).max)

    def formatRow(row: Seq[String]): String =
      row.zip(colWidths).map { case (cell, width) => cell.padTo(width, ' ') }.mkString(" | ")

    val separator = colWidths.map("-" * _).mkString("-+-")

    println(formatRow(headers))
    println(separator)
    data.foreach(row => println(formatRow(row)))
  }
}
