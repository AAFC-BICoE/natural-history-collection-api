package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionSequence;
import ca.gc.aafc.collection.api.validation.CollectionValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;

import lombok.NonNull;

@Service
public class CollectionService extends DefaultDinaService<Collection> {

  private final CollectionSequenceService collectionSequenceService;
  private final CollectionValidator collectionValidator;

  public CollectionService(
    @NonNull BaseDAO baseDAO, 
    @NonNull SmartValidator sv,
    @NonNull CollectionSequenceService collectionSequenceService,
    @NonNull CollectionValidator collectionValidator
  ) {
    super(baseDAO, sv);
    this.collectionSequenceService = collectionSequenceService;
    this.collectionValidator = collectionValidator;
  }

  @Override
  protected void preCreate(Collection entity) {
    entity.setUuid(UUID.randomUUID());
  }

  @Override
  protected void postCreate(Collection entity) {
    createAssociatedCollectionSequence(entity);
  }

  /**
   * When a collection is created, a matching collection sequence also needs to be made.
   *
   * This keeps track of the current sequence number for that specific collection.
   */
  private void createAssociatedCollectionSequence(Collection entity) {
    CollectionSequence collectionSequence = new CollectionSequence();
    collectionSequence.setCollection(entity);
    collectionSequenceService.create(collectionSequence);
  }

  @Override
  protected void preDelete(Collection entity) {
    // When a collection is deleted, the collection sequence also needs to be deleted.
    collectionSequenceService.delete(
      collectionSequenceService.findOneById(entity.getId(), CollectionSequence.class)
    );
  }

  @Override
  public void validateBusinessRules(Collection entity) {
    applyBusinessRule(entity, collectionValidator);
  }

}
