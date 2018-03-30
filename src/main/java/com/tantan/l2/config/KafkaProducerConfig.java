package com.tantan.l2.config;

import com.tantan.l2.helpers.AvroSerializer;
import com.tantan.l2.models.User;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig<T> {
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private KafkaProperties kafkaProperties;

    @Bean
    public ProducerFactory<Integer, T> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        LOGGER.info("kafkaProperties.getBootstrap is " + kafkaProperties.getBootstrap());
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrap());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AvroSerializer.class);
        return props;
    }

    @Bean
    public KafkaTemplate<Integer, T> kafkaTemplate() {
        return new KafkaTemplate<Integer, T>(producerFactory());
    }
}
