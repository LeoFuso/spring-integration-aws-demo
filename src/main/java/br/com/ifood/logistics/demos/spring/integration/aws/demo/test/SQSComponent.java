package br.com.ifood.logistics.demos.spring.integration.aws.demo.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SQSComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQSComponent.class);

    @SuppressWarnings("UnresolvedMessageChannel")
    @ServiceActivator(inputChannel = "inboundQueueChannel")
    public void handle1(final Message<String> message) {
        LOGGER.info("inbound -> {}", message);
    }

    @SuppressWarnings("UnresolvedMessageChannel")
    @ServiceActivator(inputChannel = "genericQueueChannel")
    public void handle2(final Message<String> message) {
        LOGGER.info("generic-> {}", message);
    }
}
