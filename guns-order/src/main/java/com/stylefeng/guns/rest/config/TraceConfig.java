package com.stylefeng.guns.rest.config;

import brave.spring.beans.TracingFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.okhttp3.OkHttpSender;

/**
 * @author : jiangzh
 * @description : trace
 **/
@Configuration
public class TraceConfig {

    @Bean("tracing")
    public TracingFactoryBean getTracingBean(){
        TracingFactoryBean tracingFactoryBean = new TracingFactoryBean();
        tracingFactoryBean.setLocalServiceName("order");
        tracingFactoryBean.setSpanReporter(AsyncReporter.create(OkHttpSender.create("http://localhost:9411/api/v2/spans")));

        return tracingFactoryBean;
    }

}
