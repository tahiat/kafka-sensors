package com.fillmore_labs.kafka.sensors.serde.confluent.json.serialization;

import static com.google.common.truth.Truth.assertThat;

import com.fillmore_labs.kafka.sensors.serde.confluent.common.Confluent;
import com.fillmore_labs.kafka.sensors.serde.confluent.common.SchemaRegistryModule;
import com.fillmore_labs.kafka.sensors.serde.json.serialization.EventJson;
import com.fillmore_labs.kafka.sensors.serde.json.serialization.EventJson.Position;
import com.fillmore_labs.kafka.sensors.serde.json.serialization.StateWithDurationJson;
import dagger.Component;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Singleton;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.Test;

public final class SerializationTest {
  private static final String TOPIC = "topic";

  private final Serializer<StateWithDurationJson> serializer;
  private final Deserializer<StateWithDurationJson> deserializer;

  public SerializationTest() {
    var testComponent = TestComponent.create();
    this.serializer = testComponent.serializer();
    this.deserializer = testComponent.deserializer();
  }

  @Test
  public void canDecode() {
    var event =
        EventJson.builder().time(Instant.ofEpochSecond(443634300L)).position(Position.ON).build();

    var sensorState =
        StateWithDurationJson.builder()
            .id("7331")
            .event(event)
            .duration(Duration.ofSeconds(15))
            .build();

    var encoded = serializer.serialize(TOPIC, sensorState);

    // Check for “Magic Byte”
    // https://docs.confluent.io/current/schema-registry/serializer-formatter.html#wire-format
    assertThat(encoded[0]).isEqualTo((byte) 0);

    var decoded = deserializer.deserialize(TOPIC, encoded);

    assertThat(decoded).isEqualTo(sensorState);
  }

  @Singleton
  @Component(modules = {SerializationModule.class, SchemaRegistryModule.class})
  public interface TestComponent {
    static TestComponent create() {
      return DaggerSerializationTest_TestComponent.create();
    }

    @Confluent
    Serializer<StateWithDurationJson> serializer();

    @Confluent
    Deserializer<StateWithDurationJson> deserializer();
  }
}
