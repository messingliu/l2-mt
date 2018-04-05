package com.tantan.l2.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import com.tantan.l2.utils.AvroDeserializer;

import com.tantan.avro.KafkaTest;

@Configuration
@EnableKafka
public class KafkaConsumerConfig {
  private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

  @Autowired
  private KafkaProperties kafkaProperties;

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    LOGGER.info("Consumer kafkaProperties.getBootstrap is " + kafkaProperties.getBootstrap());
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrap());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, AvroDeserializer.class);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "avro");

    return props;
  }

  @Bean
  public ConsumerFactory<String, KafkaTest> consumerFactory() {
    return new DefaultKafkaConsumerFactory<>(consumerConfigs(), new StringDeserializer(),
        new AvroDeserializer<>(KafkaTest.class));
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, KafkaTest> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, KafkaTest> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }
}
