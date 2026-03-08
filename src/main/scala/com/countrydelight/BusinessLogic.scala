package com.countrydelight

import Config._
import CsvUtils._
import DataProcessor._

object BusinessLogic:

  def logicCol1(line: String, index: Int): Either[String, String] =
    if index == 0 then
      Right(line + ",Calculated_Revenue (NEW)")
    else
      for
        cols <- Right(splitCsv(line))
        _    <- validateRowLength(cols, MinColumns)
        qtyStr <- getColumn(cols, 15).toRight("Missing quantity in col 15")
        qty    <- parseDouble(qtyStr).toRight(s"Invalid quantity: $qtyStr")
        priceStr <- getColumn(cols, 16).toRight("Missing price in col 16")
        price  <- parseDouble(priceStr).toRight(s"Invalid price: $priceStr")
      yield
        val result = qty * price
        line + s",$result"

  def logicCol2(line: String, index: Int): Either[String, String] =
    if index == 0 then
      Right(line + ",Stock_Difference (NEW)")
    else
      for
        cols <- Right(splitCsv(line))
        _    <- validateRowLength(cols, MinColumns)
        stockStr <- getColumn(cols, 20).toRight("Missing stock in col 20")
        stock  <- parseDouble(stockStr).toRight(s"Invalid stock: $stockStr")
        minStr <- getColumn(cols, 21).toRight("Missing min stock in col 21")
        minStk <- parseDouble(minStr).toRight(s"Invalid min stock: $minStr")
      yield
        val result = stock - minStk
        line + s",$result"

  def logicCol3(line: String, index: Int): Either[String, String] =
    if index == 0 then
      Right(line + ",Tax_7_Percent (NEW)")
    else
      for
        cols <- Right(splitCsv(line))
        _    <- validateRowLength(cols, MinColumns)
        revStr <- getColumn(cols, 17).toRight("Missing revenue in col 17")
        rev    <- parseDouble(revStr).toRight(s"Invalid revenue: $revStr")
      yield
        val result = rev * 0.07
        line + s",$result"

  def col1Seq(res: ProcessResult) = processColumnSeq(res, "Col1: Rev")(logicCol1)
  def col2Seq(res: ProcessResult) = processColumnSeq(res, "Col2: Stk")(logicCol2)
  def col3Seq(res: ProcessResult) = processColumnSeq(res, "Col3: Tax")(logicCol3)

  def col1Par(res: ProcessResult) = processColumnPar(res, "Col1: Rev")(logicCol1)
  def col2Par(res: ProcessResult) = processColumnPar(res, "Col2: Stk")(logicCol2)
  def col3Par(res: ProcessResult) = processColumnPar(res, "Col3: Tax")(logicCol3)
