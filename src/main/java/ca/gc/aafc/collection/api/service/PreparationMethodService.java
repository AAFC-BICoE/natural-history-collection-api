package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.PreparationMethod;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

// CHECKSTYLE:OFF NoFinalizer
// CHECKSTYLE:OFF SuperFinalize
@Service
public class PreparationMethodService extends DefaultDinaService<PreparationMethod> {
  public PreparationMethodService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
  }

  @Override
  protected void preCreate(PreparationMethod entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
  }

  protected final void finalize() {
    // no-op
  }
  
}
