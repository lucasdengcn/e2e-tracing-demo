package com.service.extension.telemetry;

import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizer;
import io.opentelemetry.sdk.autoconfigure.spi.AutoConfigurationCustomizerProvider;
import io.opentelemetry.sdk.autoconfigure.spi.ConfigProperties;
import io.opentelemetry.sdk.trace.SdkTracerProviderBuilder;

import java.util.logging.Logger;


public class OTELConfigurationProvider implements AutoConfigurationCustomizerProvider {

    private static final Logger log = Logger.getLogger(OTELConfigurationProvider.class.getName());

    public OTELConfigurationProvider() {
    }

    @Override
    public void customize(AutoConfigurationCustomizer autoConfiguration) {
        autoConfiguration
                .addTracerProviderCustomizer(this::configureSdkTracerProvider);
    }


    private SdkTracerProviderBuilder configureSdkTracerProvider(
            SdkTracerProviderBuilder tracerProvider, ConfigProperties config) {

        return tracerProvider
                .addSpanProcessor(new OTELSpanProcessor());
    }

    @Override
    public int order() {
        return 100;
    }

}
