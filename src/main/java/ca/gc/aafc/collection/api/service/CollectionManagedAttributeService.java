package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.ManagedAttributeService;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.web.HttpRequestMethodNotSupportedException;

import java.util.UUID;

@Service
public class CollectionManagedAttributeService extends
    ManagedAttributeService<CollectionManagedAttribute> {

  public CollectionManagedAttributeService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(CollectionManagedAttribute entity) {
    entity.setUuid(UUID.randomUUID());
    super.preCreate(entity);
  }

  @SneakyThrows
  @Override
  public void delete(CollectionManagedAttribute entity) {
    throw new HttpRequestMethodNotSupportedException("DELETE");
  }

}
