package com.ngontro86.restful.common.client;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.HttpHeaders;
import java.util.Map;

import static javax.ws.rs.client.ClientBuilder.newClient;

public class RestClient {

    private String host;
    private int port;
    private String basePath;

    private Client client;

    protected RestClient(String host, int port) {
        this(host, port, "");
    }

    protected RestClient(String host, int port, String basePath) {
        this.host = host;
        this.port = port;
        this.basePath = basePath;

        this.client = newClient();
        client.register(ObjectMapperResolver.class);
    }

    public <T> T withQueryParams(String path, Map<String, Object> queryParams, Class<T> out) {
        WebTarget webTarget = client.target(target(path));
        for (Map.Entry<String, Object> e : queryParams.entrySet()) {
            webTarget = webTarget.queryParam(e.getKey(), e.getValue());
        }
        return webTarget.request().get(out);
    }

    public <T> T post(String path, Entity entity, Class<T> out) {
        WebTarget webTarget = client.target(target(path));
        return webTarget.request().buildPost(entity).invoke(out);
    }

    public <T> T postWithQParamsAndHeader(String path, Map<String, Object> queryParams, String headerName, String headerValue, Entity entity, Class<T> out) {
        WebTarget webTarget = client.target(target(path));
        for (Map.Entry<String, Object> e : queryParams.entrySet()) {
            webTarget = webTarget.queryParam(e.getKey(), e.getValue());
        }
        return webTarget.request().header(headerName, headerValue).buildPost(entity).invoke(out);
    }

    public <T> T postWithQueryParams(String path, Map<String, Object> queryParams, Entity entity, Class<T> out) {
        WebTarget webTarget = client.target(target(path));
        for (Map.Entry<String, Object> e : queryParams.entrySet()) {
            webTarget = webTarget.queryParam(e.getKey(), e.getValue());
        }
        return webTarget.request().buildPost(entity).invoke(out);
    }

    public <T> T withQueryParamsAndBearerToken(String path, Map<String, Object> queryParams, String token, Class<T> out) {
        WebTarget webTarget = client.target(target(path));
        for (Map.Entry<String, Object> e : queryParams.entrySet()) {
            webTarget = webTarget.queryParam(e.getKey(), e.getValue());
        }
        return webTarget.request().header(HttpHeaders.AUTHORIZATION, "Bearer " + token).get(out);
    }

    public <T> T postAndBearerToken(String path, Map<String, Object> queryParams, Entity entity, String token, Class<T> out) {
        WebTarget webTarget = client.target(target(path));
        for (Map.Entry<String, Object> e : queryParams.entrySet()) {
            webTarget = webTarget.queryParam(e.getKey(), e.getValue());
        }
        Invocation.Builder builder = webTarget.request().header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return builder.buildPost(entity).invoke(out);
    }

    private String target(String path) {
        return host + (port != 80 ? (":" + port) : "") + "/" + (basePath == "" ? "" : (basePath + "/")) + path;
    }
}
