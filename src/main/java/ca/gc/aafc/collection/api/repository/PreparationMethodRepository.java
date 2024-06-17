package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.PreparationMethodDto;
import ca.gc.aafc.collection.api.entities.PreparationMethod;
import ca.gc.aafc.collection.api.security.SuperUserInGroupCUDAuthorizationService;
import ca.gc.aafc.collection.api.service.PreparationMethodService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// CHECKSTYLE:OFF NoFinalizer
// CHECKSTYLE:OFF SuperFinalize
@Repository
public class PreparationMethodRepository extends DinaRepository<PreparationMethodDto, PreparationMethod> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;

  public PreparationMethodRepository(
    @NonNull PreparationMethodService dinaService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull AuditService auditService,
    SuperUserInGroupCUDAuthorizationService authorizationService,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser,
    ObjectMapper objectMapper
  ) {
    super(
      dinaService,
      authorizationService,
      Optional.of(auditService),
      new DinaMapper<>(PreparationMethodDto.class),
      PreparationMethodDto.class,
      PreparationMethod.class,
      null,
      externalResourceProvider,
      buildProperties, objectMapper);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends PreparationMethodDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }

  protected final void finalize() {
    // no-op
  }
  
}
