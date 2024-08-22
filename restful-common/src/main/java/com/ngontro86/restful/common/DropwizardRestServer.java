package com.ngontro86.restful.common;

import com.codahale.metrics.health.HealthCheck;
import com.ngontro86.common.annotations.ConfigValue;
import com.ngontro86.common.annotations.Logging;
import com.ngontro86.common.annotations.RestService;
import com.ngontro86.common.annotations.DBOEComponent;
import com.ngontro86.utils.StringUtils;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
import io.dropwizard.auth.AuthDynamicFeature;
import io.dropwizard.auth.AuthValueFactoryProvider;
import io.dropwizard.configuration.ResourceConfigurationSourceProvider;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.federecio.dropwizard.swagger.SwaggerBundle;
import io.federecio.dropwizard.swagger.SwaggerBundleConfiguration;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.servlets.CrossOriginFilter;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import java.util.*;
import java.util.stream.Collectors;

@DBOEComponent
public class DropwizardRestServer extends Application<AbstractConfiguration> {

    @Logging
    private Logger logger;

    @Inject
    private ApplicationContext context;

    @ConfigValue(config = "restEnabled")
    private Boolean restEnabled = true;

    @ConfigValue(config = "authEnabled")
    private Boolean authEnabled = false;

    @ConfigValue(config = "authorizeEnabled")
    private Boolean authorizeEnabled = false;

    @ConfigValue
    private String ymlConfiguration = "configuration.yml";

    @ConfigValue
    private String appName = "Dropwizard Rest";

    @ConfigValue
    private Boolean assetEnabled = false;

    @ConfigValue
    private String assetPath = "/src/main/resources/assets/";

    @ConfigValue
    private String assetUrlPath = "/";

    @ConfigValue
    private Boolean swaggerEnabled = true;

    @ConfigValue
    private Boolean ymlResourced = true;

    @ConfigValue
    private Boolean corEnabled = true;

    @ConfigValue
    private String corOrigins = "*";

    @PostConstruct
    private void init() {
        try {
            if (restEnabled) {
                run("server", ymlConfiguration);
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }

    @Override
    public String getName() {
        return appName;
    }

    @Override
    public void initialize(Bootstrap<AbstractConfiguration> bootstrap) {
        if (ymlResourced) {
            bootstrap.setConfigurationSourceProvider(new ResourceConfigurationSourceProvider());
        }
        if (swaggerEnabled) {
            bootstrap.addBundle(new SwaggerBundle<AbstractConfiguration>() {
                @Override
                protected SwaggerBundleConfiguration getSwaggerBundleConfiguration(AbstractConfiguration configuration) {
                    configSwagger(configuration);
                    return configuration.swaggerConfig;
                }
            });
        }
        if (assetEnabled) {
            logger.info("Asset path: " + assetPath);
            bootstrap.addBundle(new AssetsBundle(assetPath, assetUrlPath, "index.html"));
        }
        super.initialize(bootstrap);
    }

    private void configSwagger(AbstractConfiguration configuration) {
        final Map<String, Object> apiResources = context.getBeansWithAnnotation(RestService.class);
        String resourcePath = StringUtils.join(
                apiResources
                        .values()
                        .stream()
                        .map(obj -> obj.getClass().getPackage().getName())
                        .collect(Collectors.toList()), ",");
        logger.info("Swagger path: " + resourcePath);
        configuration.swaggerConfig.setResourcePackage(resourcePath);
    }

    private void enableAuthIfRequired(Environment environment) {
        if (authEnabled) {
            logger.info("Authentication is enabled!");
            environment.jersey().register(context.getBean(AuthDynamicFeature.class));
            if (authorizeEnabled) {
                logger.info("Authorization is enabled!");
                environment.jersey().register(RolesAllowedDynamicFeature.class);
            }
            environment.jersey().register(context.getBean(AuthValueFactoryProvider.Binder.class));
        }
    }

    private void enableCorIfRequired(Environment environment) {
        if (corEnabled) {
            logger.info("CORS is enabled!");
            final FilterRegistration.Dynamic cors =
                    environment.servlets().addFilter("CORS", CrossOriginFilter.class);
            cors.setInitParameter(CrossOriginFilter.ALLOWED_ORIGINS_PARAM, corOrigins);
            cors.setInitParameter(CrossOriginFilter.ALLOWED_HEADERS_PARAM, "X-Requested-With,Content-Type,Accept,Origin,Authorization,passCode");
            cors.setInitParameter(CrossOriginFilter.ALLOWED_METHODS_PARAM, "OPTIONS,GET,PUT,POST,DELETE,HEAD");
            cors.setInitParameter(CrossOriginFilter.ALLOW_CREDENTIALS_PARAM, "true");
            cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType.class), true, "/*");
        }
    }

    @Override
    public void run(AbstractConfiguration configuration, Environment environment) {
        final Map<String, Object> restServices = context.getBeansWithAnnotation(RestService.class);
        restServices.values().stream().forEach(o -> environment.jersey().register(o));
        enableAuthIfRequired(environment);
        enableCorIfRequired(environment);
        environment.healthChecks().register("HealthCheck", new HealthCheck() {
            @Override
            protected Result check() {
                return Result.healthy();
            }
        });
    }
}
