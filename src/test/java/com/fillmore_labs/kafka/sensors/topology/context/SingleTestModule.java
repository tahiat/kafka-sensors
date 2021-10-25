package com.fillmore_labs.kafka.sensors.topology.context;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.fillmore_labs.kafka.sensors.model.SensorState;
import com.fillmore_labs.kafka.sensors.model.SensorStateDuration;
import com.fillmore_labs.kafka.sensors.topology.TopologySettings;
import dagger.Module;
import dagger.Provides;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import javax.inject.Qualifier;
import javax.inject.Scope;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;

@Module
public abstract class SingleTestModule {
  private static final String INPUT_TOPIC = "input-topic-";
  private static final String RESULT_TOPIC = "result-topic-";
  private static final String STORE_NAME = "duration-store-";

  private SingleTestModule() {}

  @Provides
  @TestRun
  /* package */ static TopologyTestDriver topologyTestDriver(Topology topology, Properties config) {
    return new TopologyTestDriver(topology, config);
  }

  @Provides
  @TestRun
  /* package */ static TestInputTopic<String, SensorState> inputTopic(
      TopologyTestDriver testDriver, TopologySettings settings) {
    return testDriver.createInputTopic(
        settings.inputTopic(), new StringSerializer(), settings.inputSerde().serializer());
  }

  @Provides
  @TestRun
  /* package */ static TestOutputTopic<String, SensorStateDuration> resultTopic(
      TopologyTestDriver testDriver, TopologySettings settings) {
    return testDriver.createOutputTopic(
        settings.resultTopic(), new StringDeserializer(), settings.resultSerde().deserializer());
  }

  @Provides
  @TestRun
  /* package */ static TopologySettings topologySettings(
      Formats formats,
      Map<String, Serde<SensorState>> serdeMap,
      Map<String, Serde<SensorStateDuration>> serdeDurationMap) {
    var random = new Random().nextInt(10000);
    var inputTopic = INPUT_TOPIC + random;
    var resultTopic = RESULT_TOPIC + random;
    var storeName = STORE_NAME + random;
    var inputSerde = serdeMap.get(formats.input());
    var storeSerde = serdeMap.get(formats.store());
    var resultSerde = serdeDurationMap.get(formats.result());

    if (inputSerde == null) {
      throw new IllegalArgumentException(String.format("Input format %s not defined", formats.input()));
    }
    if (storeSerde == null) {
      throw new IllegalArgumentException(String.format("Store format %s not defined", formats.store()));
    }
    if (resultSerde == null) {
      throw new IllegalArgumentException(String.format("Result format %s not defined", formats.result()));
    }

    return TopologySettings.builder()
        .inputSerde(inputSerde)
        .inputTopic(inputTopic)
        .storeSerde(storeSerde)
        .storeName(storeName)
        .resultSerde(resultSerde)
        .resultTopic(resultTopic)
        .build();
  }

  @Qualifier
  @Documented
  @Retention(RUNTIME)
  public @interface Input {}

  @Qualifier
  @Documented
  @Retention(RUNTIME)
  public @interface Store {}

  @Qualifier
  @Documented
  @Retention(RUNTIME)
  public @interface Result {}

  @Scope
  @Documented
  @Retention(RUNTIME)
  public @interface TestRun {}
}