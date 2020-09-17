package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties;

import javax.validation.Valid;

import java.util.Map;
import java.util.Optional;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.consumer.Consumer;

@Validated
@ConfigurationProperties("integration.aws.sqs.configuration")
public class AwsSqsQueueConfigurationProperties {

    @Valid
    private Map<String, Consumer> consumers = Map.of();

    public void setConsumers(final Map<String, Consumer> consumers) {
        this.consumers = Optional.ofNullable(consumers)
                                 .orElseGet(Map::of);

        this.consumers.forEach((beanName, consumer) -> consumer.setBeanNamePrefix(beanName));
    }

    public Map<String, Consumer> getConsumers() {
        return consumers;
    }

}
