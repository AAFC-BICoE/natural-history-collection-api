package ca.gc.aafc.collection.api.config;

import jakarta.inject.Named;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import ca.gc.aafc.dina.messaging.config.RabbitMQQueueProperties;

@ConfigurationProperties(prefix = "rabbitmq")
@Component
@Named("searchQueueProperties")
public class SearchQueueProperties extends RabbitMQQueueProperties {
}
