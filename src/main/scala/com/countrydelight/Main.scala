package com.countrydelight

import java.lang.management.ManagementFactory

import Config._
import CsvUtils._
import BusinessLogic._
import DataProcessor._

def printSystemStats(stage: String): Unit =
  val mb = 1024 * 1024
  val runtime = Runtime.getRuntime
  val usedMem = (runtime.totalMemory - runtime.freeMemory) / mb
  val maxMem = runtime.maxMemory / mb
  val threads = java.lang.Thread.activeCount()
  println(s"\n--- SysMonitor [$stage] ---")
  println(s"    Memory Used:    $usedMem MB / $maxMem MB")
  println(s"    Active Threads: $threads")
  println(s"---------------------------\n")

def handleError(msg: String): Unit =
  System.err.println(s"[FATAL] $msg")
  System.exit(1)

@main def runTwitterLab(): Unit =
  val jvmName = ManagementFactory.getRuntimeMXBean.getName
  println(s"Starting Application (JVM Process: $jvmName)")

  if !fileExists(InputFile) then handleError(s"Input file not found: $InputFile")

  printSystemStats("Initial State")

  val lines = readLines(InputFile) match
    case Left(err) => 
      handleError(err)
      Nil
    case Right(data) if data.isEmpty => 
      handleError("File is empty")
      Nil
    case Right(data) => data

  println(s"Loaded ${lines.size} lines successfully.")
  printSystemStats("After reading CSV file")

  val initialResult = ProcessResult(Nil, lines.zipWithIndex)

  println("=== Sequential Processing ===")
  val startTime = System.currentTimeMillis()

  val stepSeq =
    initialResult
      |> col1Seq
      |> col2Seq
      |> col3Seq

  writeLines(OutputSeq, stepSeq.valid.map(_._1)).left.foreach(handleError)
  if stepSeq.errors.nonEmpty then
    writeLines("malformed_seq.csv", stepSeq.errors).left.foreach(handleError)

  val seqTime = System.currentTimeMillis() - startTime
  println(f"\nSequential Time Completed in: $seqTime ms")
  println(s"Valid lines: ${stepSeq.valid.size}, Malformed lines: ${stepSeq.errors.size}")

  printSystemStats("After Sequential Phase")

  println("=== Parallel Processing ===")
  val startTime2 = System.currentTimeMillis()

  val stepPar =
    initialResult
      |> col1Par
      |> col2Par
      |> col3Par

  writeLines(OutputPar, stepPar.valid.map(_._1)).left.foreach(handleError)
  if stepPar.errors.nonEmpty then
    writeLines("malformed_par.csv", stepPar.errors).left.foreach(handleError)

  val parTime = System.currentTimeMillis() - startTime2
  println(f"\nParallel Time Completed in: $parTime ms")
  println(s"Valid lines: ${stepPar.valid.size}, Malformed lines: ${stepPar.errors.size}")

  printSystemStats("After Parallel Phase")
