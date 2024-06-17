package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.CollectionMethod;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

// CHECKSTYLE:OFF NoFinalizer
// CHECKSTYLE:OFF SuperFinalize
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
    entity.setUuid(UUIDHelper.generateUUIDv7());
  }

  protected final void finalize() {
    // no-op
  }
}
