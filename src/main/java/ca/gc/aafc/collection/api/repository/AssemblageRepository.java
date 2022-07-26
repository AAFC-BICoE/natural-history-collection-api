package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.collection.api.service.AssemblageService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.security.DinaAuthorizationService;
import ca.gc.aafc.dina.service.AuditService;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AssemblageRepository extends DinaRepository<AssemblageDto, Assemblage> {

  private final DinaAuthenticatedUser dinaAuthenticatedUser;

  public AssemblageRepository(
          @NonNull AssemblageService dinaService,
          ExternalResourceProvider externalResourceProvider,
          @NonNull AuditService auditService,
          DinaAuthorizationService groupAuthorizationService,
          @NonNull BuildProperties buildProperties,
          Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
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
            buildProperties);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser.orElse(null);
  }

  @Override
  public <S extends AssemblageDto> S create(S resource) {
    if( dinaAuthenticatedUser != null) {
      resource.setCreatedBy(dinaAuthenticatedUser.getUsername());
    }
    return super.create(resource);
  }
}
