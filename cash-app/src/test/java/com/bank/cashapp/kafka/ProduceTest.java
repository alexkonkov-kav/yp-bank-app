package com.bank.cashapp.kafka;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(
        topics = {ProduceTest.TEST_TOPIC_NAME}
)
public class ProduceTest {

    public static final String TEST_TOPIC_NAME = "test-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    public void testSimpleProcessor() {
        try (var consumerForTest = new DefaultKafkaConsumerFactory<>(
                KafkaTestUtils.consumerProps("bank-system", "true", embeddedKafkaBroker),
                new StringDeserializer(),
                new StringDeserializer()
        ).createConsumer()) {
            consumerForTest.subscribe(List.of(TEST_TOPIC_NAME));
            kafkaTemplate.send(TEST_TOPIC_NAME, "key", "data");

            var inputMessage = KafkaTestUtils.getSingleRecord(consumerForTest, TEST_TOPIC_NAME, Duration.ofSeconds(5));
            assertThat(inputMessage.key()).isEqualTo("key");
            assertThat(inputMessage.value()).isEqualTo("data");
        }
    }
}
