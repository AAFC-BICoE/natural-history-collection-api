package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import ca.gc.aafc.collection.api.security.SuperUserInGroupCUDAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.service.CollectionService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;

// CHECKSTYLE:OFF NoFinalizer
// CHECKSTYLE:OFF SuperFinalize
@Repository
public class CollectionRepository extends DinaRepository<CollectionDto, Collection> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;
  
  public CollectionRepository(
    @NonNull CollectionService dinaService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull AuditService auditService,
    SuperUserInGroupCUDAuthorizationService authService,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser,
    ObjectMapper objectMapper
  ) {
    super(
      dinaService, authService,
      Optional.of(auditService),
      new DinaMapper<>(CollectionDto.class),
      CollectionDto.class,
      Collection.class,
      null,
      externalResourceProvider,
      buildProperties, objectMapper);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends CollectionDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }

  protected final void finalize() {
    // no-op
  }
}
