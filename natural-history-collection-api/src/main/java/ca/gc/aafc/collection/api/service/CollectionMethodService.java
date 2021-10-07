package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.CollectionMethod;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class CollectionMethodService extends DefaultDinaService<CollectionMethod> {

  public CollectionMethodService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator validator
  ) {
    super(baseDAO, validator);
  }

  @Override
  protected void preCreate(CollectionMethod entity) {
    entity.setUuid(UUID.randomUUID());
  }
}
