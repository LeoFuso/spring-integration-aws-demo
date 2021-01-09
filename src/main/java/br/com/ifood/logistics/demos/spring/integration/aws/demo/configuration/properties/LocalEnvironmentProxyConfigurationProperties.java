package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties;

import javax.validation.Valid;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.local.ProxyClient;

@Validated
@ConfigurationProperties("local.aws.proxy")
public class LocalEnvironmentProxyConfigurationProperties {

    @Valid
    private final ProxyClient proxyClient;

    public LocalEnvironmentProxyConfigurationProperties(final ProxyClient proxyClient) {this.proxyClient = proxyClient;}

    public ProxyClient getProxyClient() {
        return proxyClient;
    }
}
