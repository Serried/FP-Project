package com.countrydelight

import scala.collection.parallel.CollectionConverters._

object DataProcessor:

  // Defines the |> pipeline operator
  extension[A](a: A)
    inline def |>[B](f: A => B): B = f(a)

  // Wrapper tracking accumulated errors and successfully processed valid data with its original line index
  case class ProcessResult(errors: List[String], valid: List[(String, Int)])

  def processColumnSeq(res: ProcessResult, colName: String)
                      (logic: (String, Int) => Either[String, String]): ProcessResult =
    val size = res.valid.size
    val counter = new java.util.concurrent.atomic.AtomicInteger(0)
    val startTime = System.currentTimeMillis()

    val (newErrors, newValid) = res.valid.partitionMap { case (line, originalIndex) =>
      val processed = logic(line, originalIndex) match
        case Left(err) => Left(s"Row $originalIndex: $err | Original: $line")
        case Right(v) => Right((v, originalIndex))

      val count = counter.incrementAndGet()
      if count % 20000 == 0 || count == size then
        val elapsed = System.currentTimeMillis() - startTime
        val threadName = Thread.currentThread().getName
        println(f"  [$colName - Seq] Processed $count%7d / $size%7d | Thread: $threadName%-20s | Time Elapsed: $elapsed%5d ms")

      processed
    }
    ProcessResult(res.errors ++ newErrors, newValid)

  def processColumnPar(res: ProcessResult, colName: String)
                      (logic: (String, Int) => Either[String, String]): ProcessResult =
    val size = res.valid.size
    val counter = new java.util.concurrent.atomic.AtomicInteger(0)
    val startTime = System.currentTimeMillis()

    val mapped = res.valid.par.map { case (line, originalIndex) =>
      val processedLine = logic(line, originalIndex) match
        case Left(err) => Left(s"Row $originalIndex: $err | Original: $line")
        case Right(v) => Right((v, originalIndex))

      val count = counter.incrementAndGet()
      if count % 20000 == 0 || count == size then
        val elapsed = System.currentTimeMillis() - startTime
        val threadName = Thread.currentThread().getName
        println(f"  [$colName - Par] Tasks Done $count%7d / $size%7d | Thread: $threadName%-20s | Time Elapsed: $elapsed%5d ms")

      processedLine
    }.seq.toList

    val (newErrors, newValid) = mapped.partitionMap(identity)
    ProcessResult(res.errors ++ newErrors, newValid)
