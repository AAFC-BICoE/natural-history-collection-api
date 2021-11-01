package ca.gc.aafc.collection.api.repository;

import java.util.UUID;

import javax.inject.Inject;
import javax.validation.ValidationException;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.CollectionSequenceGeneratorDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.service.CollectionSequenceMapper;
import ca.gc.aafc.collection.api.service.CollectionService;

import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;

@Repository
public class CollectionSequenceGeneratorRepository extends ResourceRepositoryBase<CollectionSequenceGeneratorDto, UUID> {

  @Inject
  private CollectionSequenceMapper collectionSequenceMapper;

  @Inject
  private CollectionService collectionService;

  public CollectionSequenceGeneratorRepository() {
    super(CollectionSequenceGeneratorDto.class);
  }

  @Override
  public <S extends CollectionSequenceGeneratorDto> S create(S resource) {

    // Using the UUID find the collection.
    Collection collection = collectionService.findOne(resource.getCollectionId(), Collection.class);
    if (collection == null) {
      throw new ValidationException("Collection with the UUID of '" + resource.getCollectionId() + "' does not exist.");
    }

    // Using the collection sequence mapper, retrieve the reserved ids.
    resource.setResult(collectionSequenceMapper.getNextId(collection.getId(), resource.getAmount()));

    return resource;
  }

  @Override
  public <S extends CollectionSequenceGeneratorDto> S save(S resource) {
    throw new MethodNotAllowedException("PUT/PATCH");
  }

  @Override
  public ResourceList<CollectionSequenceGeneratorDto> findAll(QuerySpec querySpec) {
    throw new MethodNotAllowedException("GET");
  }

  @Override
  public ResourceList<CollectionSequenceGeneratorDto> findAll(java.util.Collection ids, QuerySpec querySpec) {
    throw new MethodNotAllowedException("GET");
  }

  @Override
  public void delete(UUID id) {
    throw new MethodNotAllowedException("DELETE");
  }

  @Override
  public Class<CollectionSequenceGeneratorDto> getResourceClass() {
    return CollectionSequenceGeneratorDto.class;
  }
}
