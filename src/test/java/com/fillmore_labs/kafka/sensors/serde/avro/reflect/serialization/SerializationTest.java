package com.fillmore_labs.kafka.sensors.serde.avro.reflect.serialization;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import dagger.Component;
import java.time.Duration;
import java.time.Instant;
import org.apache.avro.AvroTypeException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.Test;

public final class SerializationTest {
  private static final Instant INSTANT = Instant.ofEpochSecond(443634300L);
  private static final String TOPIC = "topic";

  private final Serializer<StateDurationReflect> serializer;
  private final Deserializer<StateDurationReflect> deserializer;

  public SerializationTest() {
    var testComponent = TestComponent.create();
    this.serializer = testComponent.serializer();
    this.deserializer = testComponent.deserializer();
  }

  @Test
  public void canDecode() {
    var event = new EventReflect();
    event.time = INSTANT;
    event.position = EventReflect.Position.ON;

    var sensorState = new StateDurationReflect();
    sensorState.id = "7331";
    sensorState.event = event;
    sensorState.duration = Duration.ofSeconds(15);

    var encoded = serializer.serialize(TOPIC, sensorState);

    // Check for single-record format (version 1) marker
    // https://avro.apache.org/docs/current/spec.html#single_object_encoding
    assertThat(encoded[0]).isEqualTo((byte) 0xc3);
    assertThat(encoded[1]).isEqualTo((byte) 0x01);

    var decoded = deserializer.deserialize(TOPIC, encoded);

    assertThat(decoded).isEqualTo(sensorState);
  }

  @Test
  public void positionIsRequired() {
    var event = new EventReflect();
    event.time = INSTANT;

    var sensorState = new StateDurationReflect();
    sensorState.id = "7331";
    sensorState.event = event;
    sensorState.duration = Duration.ofSeconds(15);

    var ser = serializer; // effectively final (JLS §4.12.4)
    assertThrows(AvroTypeException.class, () -> ser.serialize(TOPIC, sensorState));
  }

  @Component(modules = {SerializationModule.class})
  public interface TestComponent {
    static TestComponent create() {
      return DaggerSerializationTest_TestComponent.create();
    }

    Serializer<StateDurationReflect> serializer();

    Deserializer<StateDurationReflect> deserializer();
  }
}
