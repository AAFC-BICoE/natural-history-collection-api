package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.service.CollectionService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.DinaAuthorizationService;
import lombok.NonNull;

@Repository
public class CollectionRepository extends DinaRepository<CollectionDto, Collection> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;
  
  public CollectionRepository(
    @NonNull CollectionService dinaService,
    ExternalResourceProvider externalResourceProvider,
    Optional<DinaAuthorizationService> dinaAdminOnlyAuthorizationService,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
      super(
          dinaService,
          dinaAdminOnlyAuthorizationService,
          Optional.empty(),
          new DinaMapper<>(CollectionDto.class),
          CollectionDto.class,
          Collection.class,
          null,
          externalResourceProvider,
          buildProperties);
      this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends CollectionDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }
}
