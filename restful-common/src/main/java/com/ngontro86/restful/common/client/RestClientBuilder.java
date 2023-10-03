package com.ngontro86.restful.common.client;

import com.ngontro86.common.config.Configuration;

public class RestClientBuilder {

    public static RestClient build(String clientName) {
        final String host = Configuration.config().getConfig(clientName + ".host");
        final String basePath = Configuration.config().getConfig(clientName + ".basePath");
        final int port = Configuration.config().getIntConfig(clientName + ".port");
        return new RestClient(host, port, basePath);
    }

    public static HttpsRestClient buildHttpsClient(String clientName) {
        final String host = Configuration.config().getConfig(clientName + ".host");
        final String version = Configuration.config().getConfig(clientName + ".version");
        final String basePath = Configuration.config().getConfig(clientName + ".basePath");
        final int port = Configuration.config().getIntConfig(clientName + ".port");
        return new HttpsRestClient(host, port, version, basePath);
    }

}
