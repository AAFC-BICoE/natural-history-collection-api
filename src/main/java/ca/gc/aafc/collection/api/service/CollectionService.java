package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionSequence;
import ca.gc.aafc.collection.api.entities.CollectionSequence.CollectionSequenceBuilder;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class CollectionService extends DefaultDinaService<Collection> {

  CollectionSequenceService collectionSequenceService;

  public CollectionService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
    collectionSequenceService = new CollectionSequenceService(baseDAO, sv);
  }

  @Override
  protected void preCreate(Collection entity) {
    entity.setUuid(UUID.randomUUID());

    // When a collection is created, a collection sequence needs to be created.
    CollectionSequence collectionSequence = new CollectionSequence(entity.getId());
    collectionSequenceService.create(collectionSequence);
  }

  @Override
  protected void preDelete(Collection entity) {
    // When a collection is deleted, the collection sequence also needs to be deleted.
    //CollectionSequence deleteCollectionSequence = 
  }
}
