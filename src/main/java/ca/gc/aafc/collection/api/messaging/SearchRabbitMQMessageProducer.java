package ca.gc.aafc.collection.api.messaging;

import javax.inject.Named;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import ca.gc.aafc.dina.messaging.config.RabbitMQQueueProperties;
import ca.gc.aafc.dina.messaging.message.DocumentOperationNotification;
import ca.gc.aafc.dina.messaging.producer.DocumentOperationNotificationMessageProducer;
import ca.gc.aafc.dina.messaging.producer.RabbitMQMessageProducer;

/**
 * RabbitMQ based message producer
 */
@Service
@ConditionalOnProperty(prefix = "dina.messaging", name = "isProducer", havingValue = "true")
public class SearchRabbitMQMessageProducer extends RabbitMQMessageProducer implements
  DocumentOperationNotificationMessageProducer {

  @Autowired
  public SearchRabbitMQMessageProducer(RabbitTemplate rabbitTemplate,
                                       @Named("searchQueueProperties")
                                       RabbitMQQueueProperties queueProperties) {
    super(rabbitTemplate, queueProperties);
  }

  @Override
  public void send(DocumentOperationNotification message) {
    super.send(message);
  }
}
