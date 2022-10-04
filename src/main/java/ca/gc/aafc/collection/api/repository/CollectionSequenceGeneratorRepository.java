package ca.gc.aafc.collection.api.repository;

import java.io.Serializable;
import java.util.Optional;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ca.gc.aafc.collection.api.dto.CollectionSequenceGeneratorDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionSequenceGenerationRequest;
import ca.gc.aafc.collection.api.service.CollectionSequenceGeneratorService;
import ca.gc.aafc.collection.api.service.CollectionService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.mapper.DinaMappingLayer;
import ca.gc.aafc.dina.mapper.DinaMappingRegistry;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.DinaAuthorizationService;

import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.resource.list.ResourceList;
import lombok.NonNull;

@Repository
public class CollectionSequenceGeneratorRepository extends DinaRepository<CollectionSequenceGeneratorDto, CollectionSequenceGenerationRequest> {

  @Inject
  private CollectionService collectionService;

  private final CollectionSequenceGeneratorService collectionSequenceGeneratorService;
  private final DinaMappingLayer<CollectionSequenceGeneratorDto, CollectionSequenceGenerationRequest> dinaMapper;
  private final DinaAuthorizationService groupAuthorizationService;

  public CollectionSequenceGeneratorRepository(
    @NonNull CollectionSequenceGeneratorService dinaService,
    ExternalResourceProvider externalResourceProvider,
    DinaAuthorizationService groupAuthorizationService,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser,
    ObjectMapper objectMapper
  ) {
    super(
      dinaService,
      groupAuthorizationService,
      Optional.empty(),
      new DinaMapper<>(CollectionSequenceGeneratorDto.class),
      CollectionSequenceGeneratorDto.class,
      CollectionSequenceGenerationRequest.class,
      null,
      externalResourceProvider,
      buildProperties, objectMapper);

    // Create the dina mapper for CollectionSequenceGeneratorDto to CollectionSequenceGenerationRequest.
    this.dinaMapper = new DinaMappingLayer<>(
      CollectionSequenceGeneratorDto.class, 
      new DinaMapper<>(CollectionSequenceGeneratorDto.class), 
      dinaService, 
      new DinaMappingRegistry(CollectionSequenceGeneratorDto.class)
    );
    this.collectionSequenceGeneratorService = dinaService;
    this.groupAuthorizationService = groupAuthorizationService;
  }

  /**
   * This method is completely overriding the super create method since it uses the findOne method
   * which is not supported for the repository or service.
   * 
   * The CollectionSequenceGenerationRequest is a fake entity and is not mapped to a database.
   */
  @Transactional
  @Override
  public <S extends CollectionSequenceGeneratorDto> S create(S resource) {

    // Retrieve the collections group to use to check if the current user has permission to perform this.
    Collection collection = collectionService.findOne(resource.getCollectionId(), Collection.class);
    if (collection == null) {
      throw new ResourceNotFoundException("The collection with the UUID of '" + resource.getCollectionId() + "' could not be found.");
    }
    resource.setGroup(collection.getGroup());

    // Convert the Dto into an entity.
    CollectionSequenceGenerationRequest entity = new CollectionSequenceGenerationRequest();
    dinaMapper.mapToEntity(resource, entity);
    
    // Check to ensure the current user has permission to perform this action.
    groupAuthorizationService.authorizeCreate(entity);

    // Perform the action from the service.
    collectionSequenceGeneratorService.create(entity);

    // Return the resource back to the results from the service.
    resource.setResult(entity.getResult());

    return resource;
  }

  @Override
  public <S extends CollectionSequenceGeneratorDto> S save(S resource) {
    throw new MethodNotAllowedException("PUT/PATCH");
  }

  @Override
  public CollectionSequenceGeneratorDto findOne(Serializable id, QuerySpec querySpec) {
    throw new MethodNotAllowedException("GET");
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
}
