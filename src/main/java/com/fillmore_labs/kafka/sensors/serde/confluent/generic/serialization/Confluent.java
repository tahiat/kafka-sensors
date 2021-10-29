package com.fillmore_labs.kafka.sensors.serde.confluent.generic.serialization;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import javax.inject.Qualifier;

@Qualifier
@Documented
@Retention(RUNTIME)
public @interface Confluent {
  @Qualifier
  @Documented
  @Retention(RUNTIME)
  @interface Event {}

  @Qualifier
  @Documented
  @Retention(RUNTIME)
  @interface SensorState {}

  @Qualifier
  @Documented
  @Retention(RUNTIME)
  @interface StateDuration {}
}
