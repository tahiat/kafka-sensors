package com.fillmore_labs.kafka.sensors.serde.confluent.generic.mapper;

import com.fillmore_labs.kafka.sensors.model.Reading;
import com.fillmore_labs.kafka.sensors.model.StateDuration;
import com.fillmore_labs.kafka.sensors.serde.avro.generic.serialization.StateDurationSchema;
import com.fillmore_labs.kafka.sensors.serde.serializer.mapped.BiMapper;
import com.google.errorprone.annotations.Immutable;
import java.time.Duration;
import javax.inject.Inject;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.checkerframework.checker.nullness.qual.PolyNull;

@Immutable
/* package */ final class StateDurationMapper implements BiMapper<StateDuration, GenericRecord> {
  private final BiMapper<Reading, GenericRecord> readingMapper;

  @Inject
  /* package */ StateDurationMapper(ReadingMapper readingMapper) {
    this.readingMapper = readingMapper;
  }

  @Override
  public @PolyNull GenericRecord map(@PolyNull StateDuration model) {
    if (model == null) {
      return null;
    }
    return new GenericRecordBuilder(StateDurationSchema.SCHEMA)
        .set(StateDurationSchema.FIELD_ID, model.getId())
        .set(StateDurationSchema.FIELD_READING, readingMapper.map(model.getReading()))
        .set(StateDurationSchema.FIELD_DURATION, model.getDuration().toNanos())
        .build();
  }

  @Override
  public @PolyNull StateDuration unmap(@PolyNull GenericRecord data) {
    if (data == null) {
      return null;
    }
    return StateDuration.builder()
        .id((String) data.get(StateDurationSchema.FIELD_ID))
        .reading(readingMapper.unmap((GenericRecord) data.get(StateDurationSchema.FIELD_READING)))
        .duration(Duration.ofNanos((Long) data.get(StateDurationSchema.FIELD_DURATION)))
        .build();
  }
}