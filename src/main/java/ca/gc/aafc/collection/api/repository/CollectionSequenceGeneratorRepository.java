package ca.gc.aafc.collection.api.repository;

import java.io.Serializable;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.ValidationException;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.CollectionSequenceGeneratorDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionSequence;
import ca.gc.aafc.collection.api.service.CollectionSequenceMapper;
import ca.gc.aafc.collection.api.service.CollectionSequenceService;
import ca.gc.aafc.collection.api.service.CollectionService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.DinaAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;
import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.resource.list.ResourceList;
import lombok.NonNull;

@Repository
public class CollectionSequenceGeneratorRepository extends DinaRepository<CollectionSequenceGeneratorDto, CollectionSequence> {

  @Inject
  private CollectionSequenceMapper collectionSequenceMapper;

  @Inject
  private CollectionService collectionService;

  @Inject
  private CollectionSequenceService collectionSequenceService;

  private Optional<DinaAuthenticatedUser> authenticatedUser;  

  public CollectionSequenceGeneratorRepository(
    @NonNull CollectionSequenceService dinaService,
    ExternalResourceProvider externalResourceProvider,
    DinaAuthorizationService groupAuthorizationService,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
    super(
      dinaService,
      groupAuthorizationService,
      Optional.empty(),
      new DinaMapper<>(CollectionSequenceGeneratorDto.class),
      CollectionSequenceGeneratorDto.class,
      CollectionSequence.class,
      null,
      externalResourceProvider,
      buildProperties);

    this.authenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends CollectionSequenceGeneratorDto> S create(S resource) {

    // The collection UUID needs to be provided.
    if (resource.getCollectionId() == null) {
      throw new ValidationException("collectionId is a required attribute.");
    }

    // Using the UUID find the collection.
    Collection collection = collectionService.findOne(resource.getCollectionId(), Collection.class);
    CollectionSequence collectionSequence = collectionSequenceService.findOneById(collection.getId(), CollectionSequence.class);
    if (collection == null || collectionSequence == null) {
      throw new ValidationException("Collection/Collection Sequence with the UUID of '" + resource.getCollectionId() + "' does not exist.");
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
  public ResourceList<CollectionSequenceGeneratorDto> findAll(java.util.Collection<Serializable> ids, QuerySpec querySpec) {
    throw new MethodNotAllowedException("GET");
  }

  @Override
  public void delete(Serializable id) {
    throw new MethodNotAllowedException("DELETE");
  }

  @Override
  public Class<CollectionSequenceGeneratorDto> getResourceClass() {
    return CollectionSequenceGeneratorDto.class;
  }
}
