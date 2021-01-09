package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration;

import org.springframework.cloud.aws.autoconfigure.context.properties.AwsCredentialsProperties;
import org.springframework.cloud.aws.autoconfigure.context.properties.AwsRegionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;

import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.LocalEnvironmentProxyConfigurationProperties;
import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.local.ProxyClient;

@Profile("local")
@Configuration
public class LocalEnvironmentProxyConfiguration {

    private final LocalEnvironmentProxyConfigurationProperties properties;

    public LocalEnvironmentProxyConfiguration(final LocalEnvironmentProxyConfigurationProperties properties) {
        this.properties = properties;
    }

    @Bean
    @Primary
    public AmazonSQSAsync amazonSQSAsyncProxyBean(
            final AwsCredentialsProperties credentialsProperties,
            final AwsRegionProperties regionProperties
    ) {

        final AWSCredentialsProvider awsCredentialsProvider = new AWSCredentialsProvider() {
            @Override
            public AWSCredentials getCredentials() {
                return new AWSCredentials() {
                    @Override
                    public String getAWSAccessKeyId() {
                        return credentialsProperties.getAccessKey();
                    }

                    @Override
                    public String getAWSSecretKey() {
                        return credentialsProperties.getSecretKey();
                    }
                };
            }

            @Override
            public void refresh() {}
        };

        return AmazonSQSAsyncClientBuilder.standard()
                                          .withClientConfiguration(clientConfiguration())
                                          .withCredentials(awsCredentialsProvider)
                                          .withRegion(regionProperties.getStatic())
                                          .build();
    }

    @Bean
    public ClientConfiguration clientConfiguration() {

        final ProxyClient proxyClient = properties.getProxyClient();

        final ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setProxyPort(proxyClient.getProxyPort());
        clientConfiguration.setProxyHost(proxyClient.getProxyHost());
        clientConfiguration.setProtocol(proxyClient.getProtocol());

        return clientConfiguration;
    }

}
