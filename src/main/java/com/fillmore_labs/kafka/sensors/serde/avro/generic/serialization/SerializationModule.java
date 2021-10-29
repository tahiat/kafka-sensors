package com.fillmore_labs.kafka.sensors.serde.avro.generic.serialization;

import com.fillmore_labs.kafka.sensors.serde.serializer.avro.AvroDeserializer;
import com.fillmore_labs.kafka.sensors.serde.serializer.avro.AvroSerializer;
import com.google.common.base.Optional;
import dagger.BindsOptionalOf;
import dagger.Module;
import dagger.Provides;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.message.SchemaStore;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

@Module
public abstract class SerializationModule {
  private SerializationModule() {}

  @Provides
  @Avro.Event
  /* package */ static Serializer<GenericRecord> eventSerializer() {
    return new AvroSerializer<>(EventSchema.MODEL, EventSchema.SCHEMA);
  }

  @Provides
  @Avro.Event
  /* package */ static Deserializer<GenericRecord> eventDeserializer(
      Optional<SchemaStore> resolver) {
    return new AvroDeserializer<>(EventSchema.MODEL, EventSchema.SCHEMA, resolver.orNull());
  }

  @Provides
  @Avro.SensorState
  /* package */ static Serializer<GenericRecord> sensorStateSerializer() {
    return new AvroSerializer<>(SensorStateSchema.MODEL, SensorStateSchema.SCHEMA);
  }

  @Provides
  @Avro.SensorState
  /* package */ static Deserializer<GenericRecord> sensorStateDeserializer(
      Optional<SchemaStore> resolver) {
    return new AvroDeserializer<>(
        SensorStateSchema.MODEL, SensorStateSchema.SCHEMA, resolver.orNull());
  }

  @Provides
  @Avro.StateDuration
  /* package */ static Serializer<GenericRecord> stateDurationSerializer() {
    return new AvroSerializer<>(StateDurationSchema.MODEL, StateDurationSchema.SCHEMA);
  }

  @Provides
  @Avro.StateDuration
  /* package */ static Deserializer<GenericRecord> stateDurationDeserializer(
      Optional<SchemaStore> resolver) {
    return new AvroDeserializer<>(
        StateDurationSchema.MODEL, StateDurationSchema.SCHEMA, resolver.orNull());
  }

  @BindsOptionalOf
  /* package */ abstract SchemaStore schemaStore();
}
