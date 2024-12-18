package ca.gc.aafc.collection.api.repository;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.hateoas.EntityModel;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.IndexRefreshDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.service.IndexRefreshService;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.messaging.message.DocumentOperationNotification;
import ca.gc.aafc.dina.messaging.producer.DocumentOperationNotificationMessageProducer;
import ca.gc.aafc.dina.security.auth.DinaAdminCUDAuthorizationService;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IndexRefreshRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private BaseDAO baseDAO;

  @Inject
  private DinaAdminCUDAuthorizationService dinaAdminCUDAuthorizationService;

  @Test
  public void indexRefreshRepository_onRefreshAll_messageSent() {
    // we are not using beans to avoid the RabbitMQ part
    List<DocumentOperationNotification> messages = new ArrayList<>();
    DocumentOperationNotificationMessageProducer messageProducer = messages::add;

    IndexRefreshService service = new IndexRefreshService(messageProducer, baseDAO);
    IndexRefreshRepository repo = new IndexRefreshRepository(dinaAdminCUDAuthorizationService, service);

    materialSampleService.create(MaterialSampleFactory.newMaterialSampleNoRelationships().build());

    IndexRefreshDto dto = new IndexRefreshDto();
    dto.setDocType(MaterialSampleDto.TYPENAME);
    repo.handlePost(EntityModel.of(dto));

    assertEquals(1, messages.size());
  }

}
