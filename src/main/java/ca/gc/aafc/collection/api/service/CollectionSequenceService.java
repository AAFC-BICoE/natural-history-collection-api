package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.CollectionSequence;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;

import lombok.NonNull;

// CHECKSTYLE:OFF NoFinalizer
// CHECKSTYLE:OFF SuperFinalize
@Service
public class CollectionSequenceService extends DefaultDinaService<CollectionSequence> {

  public CollectionSequenceService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
  }

  protected final void finalize() {
    // no-op
  }
}
