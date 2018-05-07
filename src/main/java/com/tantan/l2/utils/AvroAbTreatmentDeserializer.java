package com.tantan.l2.utils;

import com.tantan.avro.abtest.ABTestingTreatmentEvent;

public class AvroAbTreatmentDeserializer extends AvroDeserializer<ABTestingTreatmentEvent> {
  public AvroAbTreatmentDeserializer() {
    super(ABTestingTreatmentEvent.class);
  }
}
