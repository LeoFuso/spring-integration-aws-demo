package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration;

import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.integration.aws.inbound.SqsMessageDrivenChannelAdapter;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.amazonaws.services.sqs.AmazonSQSAsync;

@Configuration
public class InboundQueueConfiguration {

    private final AmazonSQSAsync amazonSQS;

    public InboundQueueConfiguration(final AmazonSQSAsync amazonSQS) {
        this.amazonSQS = amazonSQS;
    }

    @Bean
    public DirectChannel inboundQueueChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer sqsMessageDrivenChannelAdapter() {

        // Inbound Adapter             -> Channel -> OutboundAdapter
        // AWS, JDBC, Kafka, RabbitMQ  -> Channel -> OutboundAdapter

        final SqsMessageDrivenChannelAdapter channelAdapter
                = new SqsMessageDrivenChannelAdapter(amazonSQS, "INBOUND_QUEUE");

        channelAdapter.setOutputChannel(inboundQueueChannel());
        channelAdapter.setMessageDeletionPolicy(SqsMessageDeletionPolicy.ON_SUCCESS);
        channelAdapter.setMaxNumberOfMessages(2);

        final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(1);
        threadPoolTaskExecutor.setMaxPoolSize(2);
        threadPoolTaskExecutor.setThreadNamePrefix("SIMPLE_PRODUCER-");
        final SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor("INBOUND-QUEUE-");
        taskExecutor.setConcurrencyLimit(2);

        channelAdapter.setTaskExecutor(taskExecutor);

        return channelAdapter;
    }

}
