package com.denisenko;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(ContainerConfig.class)
@TestPropertySource(properties = {
        "server.port=0",
        "spring.jpa.hibernate.ddl-auto=create",
        "spring.kafka.bootstrap-servers=my-cluster:9092",
        "spring.kafka.topics.reserve-stock=reserve-stock",
        "spring.kafka.topics.book-delivery=book-delivery",
        "spring.kafka.topics.payment=payment",
        "spring.kafka.topics.inventory-compensate=inventory-compensate",
        "spring.kafka.topics.delivery-compensate=delivery-compensate",
        "spring.kafka.topics.order-results=order-results",
        "spring.kafka.consumer.group-id=order-service-group"
})
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    // TODO fix it
    @Test
    public void testStartSagaThroughWebRequest() throws Exception {
        String json = """
                {
                    "orderLineItems": [
                        {
                            "itemId": "387ec43f",
                            "quantity": 2,
                            "price": 130.0
                        },
                        {
                            "itemId": "df4874ojk",
                            "quantity": 1,
                            "price": 500.0
                        }
                    ],
                    "userId": 4487,
                    "deliveryTime": "2024-12-16T20:00:00",
                    "totalPrice": 760.0
                }
                """;

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}
