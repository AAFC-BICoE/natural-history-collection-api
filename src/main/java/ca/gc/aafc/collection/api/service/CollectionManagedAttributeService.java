package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.ManagedAttributeService;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class CollectionManagedAttributeService extends
    ManagedAttributeService<CollectionManagedAttribute> {

  public CollectionManagedAttributeService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv, CollectionManagedAttribute.class);
  }

  @Override
  protected void preCreate(CollectionManagedAttribute entity) {
    entity.setUuid(UUID.randomUUID());
    super.preCreate(entity);
  }

  @SneakyThrows
  @Override
  public void delete(CollectionManagedAttribute entity) {
    throw new UnsupportedOperationException("DELETE");
  }

}
