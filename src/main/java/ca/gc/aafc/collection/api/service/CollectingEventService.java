package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.ManagedAttribute;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CollectingEventService extends DefaultDinaService<CollectingEvent> {

  public CollectingEventService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(CollectingEvent entity) {
    entity.setUuid(UUID.randomUUID());
    handleManagedAttributes(entity);
  }

  @Override
  protected void preUpdate(CollectingEvent entity) {
    if (CollectionUtils.isNotEmpty(entity.getManagedAttributes())) {
      entity.getManagedAttributes().forEach(attribute -> attribute.setAttribute(
        this.findOne(attribute.getAttribute().getUuid(), ManagedAttribute.class)));
    }
  }

  private void handleManagedAttributes(CollectingEvent entity) {
    if (CollectionUtils.isNotEmpty(entity.getManagedAttributes())) {
      entity.getManagedAttributes().forEach(attribute -> attribute.setAttribute(
        this.findOne(attribute.getAttribute().getUuid(), ManagedAttribute.class)));
    }
  }

}
