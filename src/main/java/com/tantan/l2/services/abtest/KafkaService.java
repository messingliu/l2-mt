package com.tantan.l2.services.abtest;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonParser;
import com.tantan.avro.abtest.ABTestingTreatmentEvent;
//import com.tantan.l2.models.abtest.Experiment;
//import org.apache.commons.math3.util.Pair;
import com.tantan.l2.models.abtest.Experiment;
import com.tantan.l2.utils.AvroAbTreatmentDeserializer;
import javafx.util.Pair;
import org.apache.avro.JsonProperties;
import org.apache.avro.data.Json;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.gson.GsonProperties;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@EnableKafka
public class KafkaService {
  private static final Logger LOGGER = LoggerFactory.getLogger(KafkaService.class);
  private static final String ABTEST_TREATMENT_TOPIC = "ABTestingTreatmentEvent";

  @Autowired
  private KafkaTemplate<Integer, ABTestingTreatmentEvent> testEventkafkaTemplate;

  //@Autowired
  //private AbTestService abTestService;

  public void sendABTestTreatmentEvent(long userId) {
    //Experiment experiment = abTestService.getExperiment(userId);
    //Pair<String, Integer> treatment = abTestService.getTreatment(userId, experiment);
    Experiment experiment = new Experiment();
    experiment.setExperiment_name("test experiment");
    Pair<String, Integer> treatment = new Pair<>("test treatment", 1);
    ABTestingTreatmentEvent event = new ABTestingTreatmentEvent();
    event.setUserId(userId);
    event.setExperimentName(experiment.getExperiment_name());
    event.setHashId((int) experiment.getHash_id());
    event.setTreatment(treatment.getKey());
    event.setIterationId(1);
    event.setSegmentId(treatment.getValue());
    event.setTimestamp(System.currentTimeMillis());

    testEventkafkaTemplate.send(ABTEST_TREATMENT_TOPIC, event);
  }


  @KafkaListener(topics = "ABTestingTreatmentEvent", containerFactory = "kafkaListenerContainerFactory")
  public void listen(ABTestingTreatmentEvent event) {
    try {
      System.out.println("Kafka ABTestingTreatmentEvent: ");
      System.out.println(event);
      LOGGER.info("Kafka ABTestingTreatmentEvent: {}", event);
    } catch (Exception e) {
      LOGGER.error("ABTestingTreatmentEvent receive error", e);
    }

  }


}
