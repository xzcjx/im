package com.xzccc.netty_server.conifg;


import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public MeterRegistry meterRegistry(){
        PrometheusConfig prometheusConfig = new PrometheusConfig() {

            @Override
            public String get(String s) {
                return null;
            }
        };
        return new PrometheusMeterRegistry(prometheusConfig);
    }
}
