package com.countrydelight

import java.io.{File, PrintWriter}
import scala.util.{Try, Using}

object CsvUtils:
  def parseDouble(s: String): Option[Double] =
    Try(s.trim.toDouble).toOption

  def getColumn(cols: Array[String], idx: Int): Option[String] =
    if idx >= 0 && idx < cols.length then Some(cols(idx)) else None

  def splitCsv(line: String): Array[String] =
    line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)

  def validateRowLength(cols: Array[String], minColumns: Int): Either[String, Array[String]] =
    if cols.length < minColumns then Left(s"Invalid column count: ${cols.length}")
    else Right(cols)

  def fileExists(path: String): Boolean =
    File(path).exists()

  def readLines(path: String): Either[String, List[String]] =
    Try(Using.resource(scala.io.Source.fromFile(path))(_.getLines().toList))
      .fold(
        err => Left(s"Failed to read file $path: ${err.getMessage}"),
        Right(_)
      )

  def writeLines(path: String, data: List[String]): Either[String, Unit] =
    val tmp = path + ".tmp"
    Try {
      Using.resource(PrintWriter(File(tmp))) { w =>
        w.write(data.mkString("\n"))
      }
      File(tmp).renameTo(File(path))
    }.fold(
      err => Left(s"Failed to write file $path: ${err.getMessage}"),
      _ => Right(())
    )
