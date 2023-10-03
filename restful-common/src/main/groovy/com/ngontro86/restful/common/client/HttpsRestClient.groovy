package com.ngontro86.restful.common.client

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.ws.rs.client.Client
import javax.ws.rs.client.ClientBuilder
import javax.ws.rs.client.Entity
import javax.ws.rs.client.Invocation

class HttpsRestClient {

    private String host
    private int port
    private String basePath

    private Client client

    protected HttpsRestClient(String host, int port, String version, String basePath) {
        this.host = host
        this.port = port
        this.basePath = basePath

        def hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()
        def ctx = SSLContext.getInstance(version)
        ctx.init(null, null, null)
        client = ClientBuilder.newBuilder().sslContext(ctx).hostnameVerifier(hostnameVerifier).build()
        client.register(ObjectMapperResolver.class)
    }

    public <T> T withQueryParams(String path, Map<String, Object> queryParams, String headerName, String token, Class<T> out) {
        def webTarget = client.target(target(path))
        for (Map.Entry<String, Object> e : queryParams.entrySet()) {
            webTarget = webTarget.queryParam(e.getKey(), e.getValue())
        }
        return webTarget.request().header(headerName, token).get(out)
    }

    public <T> T postWithHeaderToken(String path, Map<String, Object> queryParams, Entity entity, String headerName, String token, Class<T> out) {
        def webTarget = client.target(target(path))
        for (Map.Entry<String, Object> e : queryParams.entrySet()) {
            webTarget = webTarget.queryParam(e.getKey(), e.getValue())
        }
        Invocation.Builder builder = webTarget.request().header(headerName, token)
        return builder.buildPost(entity).invoke(out)
    }

    private String target(String path) {
        return host + (port != 80 ? (":" + port) : "") + "/" + (basePath == "" ? "" : (basePath + "/")) + path;
    }

}
