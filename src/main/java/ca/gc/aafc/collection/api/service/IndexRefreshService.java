package ca.gc.aafc.collection.api.service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.messaging.message.DocumentOperationNotification;
import ca.gc.aafc.dina.messaging.message.DocumentOperationType;
import ca.gc.aafc.dina.messaging.producer.DocumentOperationNotificationMessageProducer;

/**
 * Service used to send a notification to refresh a document in the search index.
 */
@Service
@ConditionalOnProperty(prefix = "dina.messaging", name = "isProducer", havingValue = "true")
public class IndexRefreshService {

  private static final String MAT_SAMPLE_SQL = "SELECT uuid FROM MaterialSample t ORDER BY id";
  private static final String STORAGE_SQL = "SELECT uuid FROM StorageUnit t ORDER BY id";

  private final DocumentOperationNotificationMessageProducer searchRabbitMQMessageProducer;
  private final Set<String> supportedDocumentTypes;
  private final Map<String, String> queryAllByDocumentTypes;
  private final BaseDAO baseDAO;

  public IndexRefreshService(DocumentOperationNotificationMessageProducer searchRabbitMQMessageProducer,
                             BaseDAO baseDAO) {
    this.searchRabbitMQMessageProducer = searchRabbitMQMessageProducer;
    this.baseDAO = baseDAO;

    // supported document types
    supportedDocumentTypes = Set.of(MaterialSampleDto.TYPENAME, StorageUnitDto.TYPENAME);
    queryAllByDocumentTypes = Map.of(
      MaterialSampleDto.TYPENAME, MAT_SAMPLE_SQL,
      StorageUnitDto.TYPENAME, STORAGE_SQL
    );
  }

  public void reindexDocument(String docId, String type) {
    if (!supportedDocumentTypes.contains(type)) {
      throw new IllegalStateException("Unsupported document type");
    }

    DocumentOperationNotification don = DocumentOperationNotification.builder()
      .documentId(docId)
      .documentType(type)
      .operationType(DocumentOperationType.UPDATE).build();
    searchRabbitMQMessageProducer.send(don);
  }

  public void reindexAll(String type) {

    if (!queryAllByDocumentTypes.containsKey(type)) {
      throw new IllegalStateException("Unsupported document type");
    }

    Stream<UUID> objStream =
      baseDAO.streamAllByQuery(UUID.class, queryAllByDocumentTypes.get(type), null);

    objStream.forEach(uuid -> {
      DocumentOperationNotification don = DocumentOperationNotification.builder()
        .documentId(uuid.toString())
        .documentType(type)
        .operationType(DocumentOperationType.UPDATE).build();
      searchRabbitMQMessageProducer.send(don);
    });
  }
}
