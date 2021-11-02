package ca.gc.aafc.collection.api.repository;

import java.io.Serializable;
import java.util.Optional;
import javax.inject.Inject;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.CollectionSequenceGeneratorDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.CollectionSequenceGenerationRequest;
import ca.gc.aafc.collection.api.service.CollectionSequenceGeneratorService;
import ca.gc.aafc.collection.api.service.CollectionService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.DinaAuthorizationService;

import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.resource.list.ResourceList;
import lombok.NonNull;

@Repository
public class CollectionSequenceGeneratorRepository extends DinaRepository<CollectionSequenceGeneratorDto, CollectionSequenceGenerationRequest> {

  @Inject
  private CollectionService collectionService;

  public CollectionSequenceGeneratorRepository(
    @NonNull CollectionSequenceGeneratorService dinaService,
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
      CollectionSequenceGenerationRequest.class,
      null,
      externalResourceProvider,
      buildProperties);
  }

  @Override
  public <S extends CollectionSequenceGeneratorDto> S create(S resource) {

    // Retrieve the collections group to use to check if the current user has permission to perform this.
    Collection collection = collectionService.findOne(resource.getCollectionId(), Collection.class);
    if (collection != null) {
      resource.setGroup(collection.getGroup());
    }

    return super.create(resource);
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
