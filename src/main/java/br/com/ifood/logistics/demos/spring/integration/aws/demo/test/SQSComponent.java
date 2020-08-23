package br.com.ifood.logistics.demos.spring.integration.aws.demo.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class SQSComponent {

    private static final Logger LOGGER = LoggerFactory.getLogger(SQSComponent.class);

    @SqsListener("GENERIC_QUEUE")
    public void handle(final Message<String> message) {

        final String payload = message.getPayload();
        LOGGER.info(payload);
        LOGGER.info("{}", message);

    }

    @ServiceActivator(inputChannel = "inboundQueueChannel")
    public void handle2(final Message<String> message) {
        LOGGER.info("{}", message);
    }
}
