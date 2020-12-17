/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.test

import org.apache.spark.SparkConf
import org.scalatest.FunSpec
import org.apache.spark.sql.Dataset

/**
 * The purpose of this suite is to make sure that generic FunSpec-based scala
 * tests work with a shared spark session
 */
class GenericFunSpecSuite extends FunSpec with SharedSparkSessionBase {
  import testImplicits._

  override def sparkConf: SparkConf =
    super.sparkConf
      .setAppName("test")
      .set("spark.sql.parquet.columnarReaderBatchSize", "4096")
      .set("spark.sql.sources.useV1SourceList", "avro")
      .set("spark.sql.extensions", "com.intel.oap.ColumnarPlugin")
      .set("spark.sql.execution.arrow.maxRecordsPerBatch", "4096")
      .set("spark.memory.offHeap.enabled", "true")
      .set("spark.memory.offHeap.size", "10m")
      .set("spark.sql.join.preferSortMergeJoin", "false")
      .set("spark.sql.columnar.codegen.hashAggregate", "false")
      .set("spark.oap.sql.columnar.wholestagecodegen", "false")
      .set("spark.sql.columnar.window", "false")
      .set("spark.unsafe.exceptionOnMemoryLeak", "false")
      .set("spark.sql.columnar.sort.broadcastJoin", "true")
      .set("spark.oap.sql.columnar.preferColumnar", "true")

  private def ds = Seq((1, 1), (2, 1), (3, 2), (4, 2), (5, 3), (6, 3), (7, 4), (8, 4)).toDS

  describe("Simple Dataset") {
    it("should have the specified number of elements") {
      assert(8 === ds.count)
    }
    it("should have the specified number of unique elements") {
      assert(8 === ds.distinct.count)
    }
    it("should have the specified number of elements in each column") {
      assert(8 === ds.select("_1").count)
      assert(8 === ds.select("_2").count)
    }
    it("should have the correct number of distinct elements in each column") {
      assert(8 === ds.select("_1").distinct.count)
      assert(4 === ds.select("_2").distinct.count)
    }
  }
}
