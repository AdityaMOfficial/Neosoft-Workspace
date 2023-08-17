package com.programmingmicroservice.notification;

import com.programmingmicroservice.orderservice.events.OrderPlacedEvent;
import io.opentelemetry.exporter.otlp.http.trace.OtlpHttpSpanExporter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;

@SpringBootApplication
@Slf4j
public class NotificationApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotificationApplication.class, args);
	}


	@KafkaListener(topics = "notificationTopic",groupId = "notificationId")
	public void handleNotification(OrderPlacedEvent orderPlacedEvent){

		log.info("Recieved notification for order no. :: {}",orderPlacedEvent);
	}

	@Bean
	OtlpHttpSpanExporter otlpHttpSpanExporter(@Value("${tracing.url}") String url) {
		return OtlpHttpSpanExporter.builder()
				.setEndpoint(url)
				.build();
	}

}
