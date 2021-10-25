package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionSequence;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;

import lombok.NonNull;

@Service
public class CollectionService extends DefaultDinaService<Collection> {

  private final CollectionSequenceService collectionSequenceService;

  public CollectionService(
    @NonNull BaseDAO baseDAO, 
    @NonNull SmartValidator sv,
    @NonNull CollectionSequenceService collectionSequenceService) {
    super(baseDAO, sv);
    this.collectionSequenceService = collectionSequenceService;
  }

  @Override
  protected void preCreate(Collection entity) {
    entity.setUuid(UUID.randomUUID());
  }

  /**
   * When a collection is created, a matching collection sequence also needs to be made.
   * 
   * This keeps track of the current sequence number for that specific collection.
   */
  private void createAssociatedCollectionSequence(int id) {
    CollectionSequence collectionSequence = new CollectionSequence(id);
    collectionSequenceService.createAndFlush(collectionSequence);    
  }

  @Override
  public Collection create(Collection entity) {
    super.create(entity);

    // When a collection is created, a collection sequence needs to be created.
    createAssociatedCollectionSequence(entity.getId());

    return entity;
  }

  @Override
  public Collection createAndFlush(Collection entity) {
    super.createAndFlush(entity);

    // When a collection is created, a collection sequence needs to be created.
    createAssociatedCollectionSequence(entity.getId());

    return entity;
  }

  @Override
  protected void preDelete(Collection entity) {
    // When a collection is deleted, the collection sequence also needs to be deleted.
    collectionSequenceService.delete(
      collectionSequenceService.findOneById(entity.getId(), CollectionSequence.class)
    );
  }

}
