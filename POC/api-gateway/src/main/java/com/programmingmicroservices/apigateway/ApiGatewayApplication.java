package com.programmingmicroservices.apigateway;

import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}


	@Bean
	OtlpHttpSpanExporter otlpHttpSpanExporter(@Value("${tracing.url}") String url) {
		return OtlpHttpSpanExporter.builder()
				.setEndpoint(url)
				.build();
	}

}
