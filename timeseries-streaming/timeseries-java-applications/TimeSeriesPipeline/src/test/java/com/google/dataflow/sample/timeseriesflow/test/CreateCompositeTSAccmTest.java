/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.dataflow.sample.timeseriesflow.test;

import com.google.dataflow.sample.timeseriesflow.TimeSeriesData.TSDataPoint;
import com.google.dataflow.sample.timeseriesflow.TimeSeriesData.TSKey;
import com.google.dataflow.sample.timeseriesflow.combiners.typeone.TSNumericCombiner;
import com.google.dataflow.sample.timeseriesflow.transforms.CreateCompositeTSAccum;
import com.google.dataflow.sample.timeseriesflow.transforms.GenerateComputations;
import com.google.gson.stream.JsonReader;
import common.TSTestData;
import java.io.File;
import java.io.FileReader;
import org.apache.beam.sdk.testing.PAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestStream;
import org.apache.beam.sdk.transforms.FlatMapElements;
import org.apache.beam.sdk.transforms.Keys;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.apache.beam.vendor.guava.v26_0_jre.com.google.common.collect.ImmutableList;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CreateCompositeTSAccmTest {

  @Rule public final transient TestPipeline p = TestPipeline.create();

  static final Instant NOW = Instant.parse("2000-01-01T00:00:00Z");

  /** */
  @Test
  public void testKeyCreation() throws Exception {

    String resourceName = "CreateCompositeTSAccumTest.json";
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(resourceName).getFile());
    String absolutePath = file.getAbsolutePath();

    TSTestData tsTestData =
        TSTestData.toBuilder()
            .setInputTSDataFromJSON(
                new JsonReader(new FileReader(absolutePath)),
                Duration.standardSeconds(5),
                Duration.standardSeconds(5))
            .build();

    TestStream<TSDataPoint> stream = tsTestData.inputTSData();

    GenerateComputations generateComputations =
        GenerateComputations.builder()
            .setType1FixedWindow(Duration.standardSeconds(5))
            .setType2SlidingWindowDuration(Duration.standardSeconds(5))
            .setType1NumericComputations(ImmutableList.of(new TSNumericCombiner()))
            .setType1KeyMerge(
                ImmutableList.of(
                    CreateCompositeTSAccum.builder()
                        .setKeysToCombineList(
                            ImmutableList.of(TSDataTestUtils.KEY_A_A, TSDataTestUtils.KEY_A_B))
                        .build()))
            .build();

    PCollection<TSDataPoint> testStream = p.apply(stream);

    PCollection<TSKey> result = testStream.apply(generateComputations).apply(Keys.create());

    TSKey compositeKey =
        TSDataTestUtils.KEY_A_A.toBuilder().setMinorKeyString("MKey-a-MKey-b").build();

    result.apply(new Print<>());

    PAssert.that(result)
        .inWindow(
            new IntervalWindow(
                Instant.ofEpochMilli(TSDataTestUtils.START), Duration.standardSeconds(5)))
        .containsInAnyOrder(TSDataTestUtils.KEY_A_A, TSDataTestUtils.KEY_A_B, compositeKey);

    p.run();
  }

  @Test
  public void testValueCreation() throws Exception {

    String resourceName = "CreateCompositeTSAccumTest.json";
    ClassLoader classLoader = getClass().getClassLoader();
    File file = new File(classLoader.getResource(resourceName).getFile());
    String absolutePath = file.getAbsolutePath();

    TSTestData tsTestData =
        TSTestData.toBuilder()
            .setInputTSDataFromJSON(
                new JsonReader(new FileReader(absolutePath)),
                Duration.standardSeconds(5),
                Duration.standardSeconds(5))
            .build();

    TestStream<TSDataPoint> stream = tsTestData.inputTSData();

    GenerateComputations generateComputations =
        GenerateComputations.builder()
            .setType1FixedWindow(Duration.standardSeconds(5))
            .setType2SlidingWindowDuration(Duration.standardSeconds(5))
            .setType1NumericComputations(ImmutableList.of(new TSNumericCombiner()))
            .setType1KeyMerge(
                ImmutableList.of(
                    CreateCompositeTSAccum.builder()
                        .setKeysToCombineList(
                            ImmutableList.of(TSDataTestUtils.KEY_A_A, TSDataTestUtils.KEY_A_B))
                        .build()))
            .build();

    PCollection<TSDataPoint> testStream = p.apply(stream);

    PCollection<String> result =
        testStream
            .apply(generateComputations)
            .apply(Values.create())
            .apply(
                FlatMapElements.into(TypeDescriptors.strings())
                    .via(x -> x.getDataStoreMap().keySet()));

    result.apply(new Print<>());

    PAssert.that(result)
        .inWindow(
            new IntervalWindow(
                Instant.ofEpochMilli(TSDataTestUtils.START), Duration.standardSeconds(5)))
        .containsInAnyOrder(
            "DATA_POINT_COUNT",
            "FIRST_TIMESTAMP",
            "FIRST",
            "LAST_TIMESTAMP",
            "LAST",
            "SUM",
            "MIN",
            "MAX",
            "DATA_POINT_COUNT",
            "FIRST_TIMESTAMP",
            "FIRST",
            "LAST_TIMESTAMP",
            "LAST",
            "SUM",
            "MIN",
            "MAX",
            "MKey-b-DATA_POINT_COUNT",
            "MKey-b-FIRST_TIMESTAMP",
            "MKey-b-FIRST",
            "MKey-b-LAST_TIMESTAMP",
            "MKey-b-LAST",
            "MKey-b-SUM",
            "MKey-b-MIN",
            "MKey-b-MAX",
            "MKey-a-DATA_POINT_COUNT",
            "MKey-a-FIRST_TIMESTAMP",
            "MKey-a-FIRST",
            "MKey-a-LAST_TIMESTAMP",
            "MKey-a-LAST",
            "MKey-a-SUM",
            "MKey-a-MIN",
            "MKey-a-MAX");

    p.run();
  }
}
