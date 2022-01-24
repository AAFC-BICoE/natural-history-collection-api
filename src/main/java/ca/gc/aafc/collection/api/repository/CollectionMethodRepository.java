package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.CollectionMethodDto;
import ca.gc.aafc.collection.api.entities.CollectionMethod;
import ca.gc.aafc.collection.api.service.CollectionMethodService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.GroupAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CollectionMethodRepository extends DinaRepository<CollectionMethodDto, CollectionMethod> {

  private final DinaAuthenticatedUser dinaAuthenticatedUser;

  public CollectionMethodRepository(
    @NonNull CollectionMethodService collectionMethodService,
    @NonNull GroupAuthorizationService groupAuthorizationService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull AuditService auditService,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
    super(
      collectionMethodService,
      groupAuthorizationService,
      Optional.of(auditService),
      new DinaMapper<>(CollectionMethodDto.class),
      CollectionMethodDto.class,
      CollectionMethod.class,
      null,
      externalResourceProvider,
      buildProperties);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser.orElse(null);
  }

  @Override
  public <S extends CollectionMethodDto> S create(S resource) {
    if (dinaAuthenticatedUser != null) {
      resource.setCreatedBy(dinaAuthenticatedUser.getUsername());
    }
    return super.create(resource);
  }
}
