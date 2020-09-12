package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.integration.aws.inbound.SqsMessageDrivenChannelAdapter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.amazonaws.services.sqs.AmazonSQSAsync;

import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.queue.Queue;
import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.task.Pool;
import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.task.Shutdown;
import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.task.TaskExecutor;

import static br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.AwsSqsQueueConfigurationProperties.Consumer;

public final class AwsSqsQueueAutoConfigurationUtils {

    private AwsSqsQueueAutoConfigurationUtils() {
        throw new UnsupportedOperationException("Trying to instantiate utility class");
    }

    static void registerChannelAdapter(final Consumer consumer, final GenericApplicationContext registry) {

        final String[] queues = consumer.getQueues()
                                        .stream()
                                        .map(Queue::getName)
                                        .toArray(String[]::new);

        final BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(SqsMessageDrivenChannelAdapter.class);

        final AmazonSQSAsync amazonSQS = registry.getBean(AmazonSQSAsync.class);
        builder.addConstructorArgValue(amazonSQS);
        builder.addConstructorArgValue(queues);

        builder.addPropertyValue("messageDeletionPolicy", consumer.getDeletionPolicy());
        builder.addPropertyValue("maxNumberOfMessages", consumer.getMaxNumberOfMessages());

        final Duration visibilityTimeout = consumer.getVisibilityTimeout();
        builder.addPropertyValue("visibilityTimeout", visibilityTimeout.toSeconds());

        final Duration waitTimeout = consumer.getWaitTimeout();
        builder.addPropertyValue("waitTimeOut", waitTimeout.toSeconds());

        final String channelName = consumer.getChannelName();
        final DirectChannel channel = registry.getBean(channelName, DirectChannel.class);
        builder.addPropertyValue("outputChannel", channel);

        Optional.ofNullable(consumer.getErrorChannelName())
                .ifPresent(errorChannelName -> {

                    registerChannel(errorChannelName, registry);
                    final DirectChannel errorChannel = registry.getBean(errorChannelName, DirectChannel.class);
                    builder.addPropertyValue("errorChannel", errorChannel);

                });

        final String taskExecutorName = consumer.getExecutor()
                                                .getBeanName();

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

    static void registerChannel(final String channelName, final BeanDefinitionRegistry registry) {
        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(DirectChannel.class);
        final AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(channelName, beanDefinition);
    }

    static void registerAsyncTaskExecutor(final Consumer consumer, final GenericApplicationContext registry) {

        final TaskExecutor executor = consumer.getExecutor();
        final Pool pool = executor.getPool();

        final BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ThreadPoolTaskExecutor.class);
        builder.addPropertyValue("corePoolSize", pool.getCoreSize());
        builder.addPropertyValue("maxPoolSize", pool.getMaxSize());
        builder.addPropertyValue("allowCoreThreadTimeOut", pool.isAllowCoreThreadTimeout());
        builder.addPropertyValue("queueCapacity", pool.getQueueCapacity());

        final Duration keepAlive = pool.getKeepAlive();
        builder.addPropertyValue("keepAliveSeconds", keepAlive.getSeconds());

        final Shutdown shutdown = executor.getShutdown();
        final Duration awaitTerminationPeriod = shutdown.getAwaitTerminationPeriod();
        builder.addPropertyValue("awaitTerminationSeconds", awaitTerminationPeriod.getSeconds());
        builder.addPropertyValue("waitForTasksToCompleteOnShutdown", shutdown.isAwaitTermination());

        builder.addPropertyValue("threadNamePrefix", executor.getThreadNamePrefix());

        final String beanName = executor.getBeanName();
        final BeanDefinition beanDefinition = builder.getBeanDefinition();
        registry.registerBeanDefinition(beanName, beanDefinition);

    }
}
