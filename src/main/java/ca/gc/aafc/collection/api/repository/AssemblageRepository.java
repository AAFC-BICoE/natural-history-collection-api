package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.collection.api.service.AssemblageService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.auth.DinaAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// CHECKSTYLE:OFF NoFinalizer
// CHECKSTYLE:OFF SuperFinalize
@Repository
public class AssemblageRepository extends DinaRepository<AssemblageDto, Assemblage> {

  private final DinaAuthenticatedUser dinaAuthenticatedUser;

  public AssemblageRepository(
          @NonNull AssemblageService dinaService,
          ExternalResourceProvider externalResourceProvider,
          @NonNull AuditService auditService,
          DinaAuthorizationService groupAuthorizationService,
          @NonNull BuildProperties buildProperties,
          Optional<DinaAuthenticatedUser> dinaAuthenticatedUser,
          ObjectMapper objectMapper
  ) {
    super(
            dinaService,
            groupAuthorizationService,
            Optional.of(auditService),
            new DinaMapper<>(AssemblageDto.class),
            AssemblageDto.class,
            Assemblage.class,
            null,
            externalResourceProvider,
            buildProperties, objectMapper);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser.orElse(null);
  }

  @Override
  public <S extends AssemblageDto> S create(S resource) {
    if (dinaAuthenticatedUser != null) {
      resource.setCreatedBy(dinaAuthenticatedUser.getUsername());
    }
    return super.create(resource);
  }

  protected final void finalize() {
    // no-op
  }
}
