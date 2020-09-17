package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.AwsSqsQueueConfigurationProperties;

import static br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.AwsSqsQueueAutoConfigurationRegistryUtils.registerAsyncTaskExecutor;
import static br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.AwsSqsQueueAutoConfigurationRegistryUtils.registerChannelAdapter;

@Configuration
@EnableConfigurationProperties(AwsSqsQueueConfigurationProperties.class)
public class AwsSqsQueueAutoConfiguration {

    private final AwsSqsQueueConfigurationProperties properties;

    public AwsSqsQueueAutoConfiguration(final GenericApplicationContext registry,
                                        final AwsSqsQueueConfigurationProperties properties) {
        this.properties = properties;

        registerBeanDefinitions(registry);
    }

    private void registerBeanDefinitions(final GenericApplicationContext registry) {
        properties.getConsumers()
                  .forEach((ignored, consumer) -> {

                      registerAsyncTaskExecutor(consumer, registry);
                      registerChannelAdapter(consumer, registry);

                  });
    }
}
