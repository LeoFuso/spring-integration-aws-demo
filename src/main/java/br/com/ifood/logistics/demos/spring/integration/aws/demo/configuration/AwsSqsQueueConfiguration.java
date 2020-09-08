package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration;

import java.time.Duration;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.integration.aws.inbound.SqsMessageDrivenChannelAdapter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.amazonaws.services.sqs.AmazonSQSAsync;

import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.AwsSqsQueueConfigurationProperties.Consumer;
import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.AwsSqsQueueConfigurationProperties.Queue;
import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.AwsSqsQueueConfigurationProperties.TaskExecutor;

@Configuration
@EnableConfigurationProperties(AwsSqsQueueConfigurationProperties.class)
public class AwsSqsQueueConfiguration {

    private final GenericApplicationContext context;

    private final AmazonSQSAsync amazonSQS;
    private final AwsSqsQueueConfigurationProperties properties;

    public AwsSqsQueueConfiguration(final GenericApplicationContext context,
                                    final AmazonSQSAsync amazonSQS,
                                    final AwsSqsQueueConfigurationProperties properties) {
        this.context = context;
        this.amazonSQS = amazonSQS;
        this.properties = properties;

        postProcessBeanDefinitionRegistry();
    }


    public void postProcessBeanDefinitionRegistry() throws BeansException {
        properties.getConsumers()
                  .forEach((beanName, consumer) -> {

                      final TaskExecutor executorProperties = consumer.getExecutor();
                      final ThreadPoolTaskExecutor executor = produceExecutor(beanName, executorProperties);
                      final MessageChannel channel = configureChannel(beanName, context);

                      final MessageProducer messageProducer = produceChannelAdapter(consumer, executor, channel);

                      final String channelAdapterBeanName = beanName + "ChannelAdapter";
                      context.registerBean(channelAdapterBeanName, MessageProducer.class, messageProducer);

                  });
    }

    private MessageProducer produceChannelAdapter(final Consumer consumer,
                                                  final AsyncTaskExecutor executor,
                                                  final MessageChannel channel) {

        final String[] queues = consumer.getQueues()
                                        .stream()
                                        .map(Queue::getName)
                                        .toArray(String[]::new);

        final SqsMessageDrivenChannelAdapter channelAdapter
                = new SqsMessageDrivenChannelAdapter(amazonSQS, queues);

        channelAdapter.setMessageDeletionPolicy(consumer.getDeletionPolicy());
        channelAdapter.setMaxNumberOfMessages(consumer.getMaxNumberOfMessages());

        final Duration visibilityTimeout = consumer.getVisibilityTimeout();
        channelAdapter.setVisibilityTimeout((int) visibilityTimeout.toSeconds());

        final Duration waitTimeout = consumer.getWaitTimeout();
        channelAdapter.setWaitTimeOut((int) waitTimeout.toSeconds());

        channelAdapter.setOutputChannel(channel);
        channelAdapter.setTaskExecutor(executor);

        return channelAdapter;
    }

    private static ThreadPoolTaskExecutor produceExecutor(final String beanName, final TaskExecutor properties) {

        final Integer corePoolSize = properties.getCorePoolSize();
        final Integer maxPoolSize = properties.getMaxPoolSize();

        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);

        executor.setThreadNamePrefix(beanName + "-");

        return executor;
    }

    private static MessageChannel configureChannel(final String beanName, final GenericApplicationContext context) {
        final String channelName = beanName + "Channel";
        final AutowireCapableBeanFactory beanFactory = context.getAutowireCapableBeanFactory();
        final DirectChannel directChannel = beanFactory.createBean(DirectChannel.class);
        beanFactory.initializeBean(directChannel, channelName);
        return directChannel;
    }
}
