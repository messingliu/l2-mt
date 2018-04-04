package com.tantan.l2.services;

import com.tantan.avro.KafkaTest;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {
    public static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @org.springframework.kafka.annotation.KafkaListener(topics = "test")
    public void listen(KafkaTest kafkatest) throws Exception {
        logger.info("Kafka consume: " + kafkatest.toString());
    }
}
