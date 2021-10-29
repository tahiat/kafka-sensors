package com.fillmore_labs.kafka.sensors.serde.confluent.generic.serialization;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertThrows;

import com.fillmore_labs.kafka.sensors.serde.avro.generic.serialization.EventSchema;
import com.fillmore_labs.kafka.sensors.serde.avro.generic.serialization.PositionSchema;
import com.fillmore_labs.kafka.sensors.serde.avro.generic.serialization.StateDurationSchema;
import com.fillmore_labs.kafka.sensors.serde.confluent.common.SchemaRegistryModule;
import dagger.Component;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Singleton;
import org.apache.avro.AvroMissingFieldException;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.junit.Test;

public final class SerializationTest {
  private static final Instant INSTANT = Instant.ofEpochSecond(443634300L);
  private static final String TOPIC = "topic";

  private final Serializer<GenericRecord> serializer;
  private final Deserializer<GenericRecord> deserializer;

  public SerializationTest() {
    var testComponent = TestComponent.create();
    this.serializer = testComponent.serializer();
    this.deserializer = testComponent.deserializer();
  }

  @Test
  public void canDecode() {
    var event =
        new GenericRecordBuilder(EventSchema.SCHEMA)
            .set(EventSchema.FIELD_TIME, INSTANT.toEpochMilli() * 1_000_000L + 1L)
            .set(EventSchema.FIELD_POSITION, PositionSchema.ENUM_OFF)
            .build();

    var sensorState =
        new GenericRecordBuilder(StateDurationSchema.SCHEMA)
            .set(StateDurationSchema.FIELD_ID, "7331")
            .set(StateDurationSchema.FIELD_EVENT, event)
            .set(StateDurationSchema.FIELD_DURATION, Duration.ofSeconds(15).toNanos())
            .build();

    var encoded = serializer.serialize(TOPIC, sensorState);

    // Check for “Magic Byte”
    // https://docs.confluent.io/current/schema-registry/serializer-formatter.html#wire-format
    assertThat(encoded[0]).isEqualTo((byte) 0);

    var decoded = deserializer.deserialize(TOPIC, encoded);

    assertThat(decoded).isEqualTo(sensorState);
  }

  @Test
  public void positionIsRequired() {
    assertThrows(
        AvroMissingFieldException.class,
        () ->
            new GenericRecordBuilder(EventSchema.SCHEMA)
                .set(EventSchema.FIELD_TIME, INSTANT.toEpochMilli() * 1_000_000L + 1L)
                .build());
  }

  @Test
  @SuppressWarnings("nullness:argument")
  public void notNull() {
    assertThrows(
        AvroRuntimeException.class,
        () ->
            new GenericRecordBuilder(EventSchema.SCHEMA)
                .set(EventSchema.FIELD_TIME, INSTANT.toEpochMilli() * 1_000_000L + 1L)
                .set(EventSchema.FIELD_POSITION, null)
                .build());
  }

  @Singleton
  @Component(modules = {SerializationModule.class, SchemaRegistryModule.class})
  public interface TestComponent {
    static TestComponent create() {
      return DaggerSerializationTest_TestComponent.create();
    }

    @Confluent.StateDuration
    Serializer<GenericRecord> serializer();

    @Confluent.StateDuration
    Deserializer<GenericRecord> deserializer();
  }
}
