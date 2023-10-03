package com.ngontro86.auth.client

import com.ngontro86.common.annotations.ConfigValue
import com.ngontro86.common.annotations.Logging
import com.ngontro86.common.annotations.DBOEComponent
import com.ngontro86.common.config.MaskedConfig
import com.ngontro86.restful.common.client.RestClient
import com.ngontro86.restful.common.client.RestClientBuilder
import com.ngontro86.utils.AESUtils
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.apache.logging.log4j.Logger

import javax.annotation.PostConstruct
import javax.ws.rs.client.Entity

@DBOEComponent
class AuthRestClient {

    @Logging
    private Logger logger

    @ConfigValue(config = "useremail")
    private String email

    @ConfigValue(config = "userpassword")
    private MaskedConfig userpassword

    private RestClient restClient

    @ConfigValue(config = "authRestClientName")
    private String authRestClientName

    @ConfigValue(config = "serverSignPath")
    private String serverSignPath = "user/serverSignToken"

    private String serverSignedToken

    @PostConstruct
    private void init() {
        logger.info("Authenticating first, email: ${email}, password: NOT DISPLAY...")
        restClient = RestClientBuilder.build(authRestClientName)

        def initKey = initToken(email, userpassword.getUnmaskedValue())
        serverSignedToken = restClient.withQueryParams("${serverSignPath}/${email}/${initKey}",
                [:], String)
        logger.info("Server signed token: ${serverSignedToken}")
    }

    private String initToken(String email, String password) {
        def now = new Date()
        def expiry = new Date()
        expiry.setTime(now.getTime() + 60000 * 1000)
        return Jwts.builder().setIssuer("Client")
                .setSubject("${email} Init Token")
                .setExpiration(expiry)
                .addClaims(
                        [
                                'password': password,
                                'email'   : email
                        ])
                .signWith(SignatureAlgorithm.HS256, password)
                .compact()
    }

    def <T> T withQueryParams(String path, Map<String, Object> queryParams, Class<T> out) {
        return restClient.withQueryParamsAndBearerToken(path, queryParams, serverSignedToken, out)
    }

    def <T> T postWithQueryParams(String path, Map<String, Object> queryParams, Entity entity, Class<T> out) {
        return restClient.postAndBearerToken(path, queryParams, entity, serverSignedToken, out)
    }

    String decrypt(String text) {
        return AESUtils.decrypt(userpassword.getUnmaskedValue(), text)
    }
}
