package br.com.ifood.logistics.demos.spring.integration.aws.demo.configuration.properties.local;



import com.amazonaws.Protocol;

public class ProxyClient {

    private String proxyHost = "localhost";
    private int proxyPort = 4566;
    private Protocol protocol = Protocol.HTTP;

    public String getProxyHost() {
        return proxyHost;
    }

    public void setProxyHost(final String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public void setProxyPort(final int proxyPort) {
        this.proxyPort = proxyPort;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(final Protocol protocol) {
        this.protocol = protocol;
    }
}
