package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration;

import java.time.Duration;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.integration.aws.inbound.SqsMessageDrivenChannelAdapter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.amazonaws.services.sqs.AmazonSQS;

import static br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.AwsSqsQueueConfigurationProperties.Consumer;
import static br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.AwsSqsQueueConfigurationProperties.Queue;
import static br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.AwsSqsQueueConfigurationProperties.TaskExecutor;

public final class AwsSqsQueueAutoConfigurationUtils {

    private AwsSqsQueueAutoConfigurationUtils() {
        throw new UnsupportedOperationException("Trying to instantiate Utils class");
    }

    static void registerChannelAdapter(final Consumer consumer, final GenericApplicationContext registry) {

        final String[] queues = consumer.getQueues()
                                        .stream()
                                        .map(Queue::getName)
                                        .toArray(String[]::new);

        final BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(SqsMessageDrivenChannelAdapter.class);

        final AmazonSQS amazonSQS = registry.getBean(AmazonSQS.class);
        builder.addConstructorArgValue(amazonSQS);
        builder.addConstructorArgValue(queues);

        builder.addPropertyValue("messageDeletionPolicy", consumer.getDeletionPolicy());
        builder.addPropertyValue("maxNumberOfMessages", consumer.getMaxNumberOfMessages());

        final Duration visibilityTimeout = consumer.getVisibilityTimeout();
        builder.addPropertyValue("visibilityTimeout", (int) visibilityTimeout.toSeconds());

        final Duration waitTimeout = consumer.getWaitTimeout();
        builder.addPropertyValue("waitTimeOut", (int) waitTimeout.toSeconds());

        final String channelName = consumer.getChannelName();
        final DirectChannel channel = registry.getBean(channelName, DirectChannel.class);
        builder.addPropertyValue("outputChannel", channel);

        final String taskExecutorName = consumer.getExecutor()
                                                .getName();
        final ThreadPoolTaskExecutor taskExecutor = registry.getBean(taskExecutorName, ThreadPoolTaskExecutor.class);
        builder.addPropertyValue("taskExecutor", taskExecutor);

        final String beanName = consumer.getChannelAdapterName();
        final BeanDefinition channelAdapterBeanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(beanName, channelAdapterBeanDefinition);
    }

    static void registerChannel(final Consumer consumer, final BeanDefinitionRegistry registry) {
        final String beanName = consumer.getChannelName();
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DirectChannel.class);
        final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    static void registerAsyncTaskExecutor(final Consumer consumer, final GenericApplicationContext registry) {
        final TaskExecutor executor = consumer.getExecutor();

        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ThreadPoolTaskExecutor.class);
        builder.addPropertyValue("corePoolSize", executor.getCorePoolSize());
        builder.addPropertyValue("maxPoolSize", executor.getMaxPoolSize());
        builder.addPropertyValue("threadNamePrefix", consumer.getName() + "-");

        final String beanName = executor.getName();
        final BeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(beanName, beanDefinition);
    }
}
