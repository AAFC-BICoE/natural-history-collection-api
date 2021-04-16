package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.service.GeoReferenceAssertionService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.AuditService;
import lombok.NonNull;

@Repository
public class GeoreferenceAssertionRepository extends DinaRepository<GeoreferenceAssertionDto, GeoreferenceAssertion> {

  private Optional<DinaAuthenticatedUser> authenticatedUser;  

    public GeoreferenceAssertionRepository(
    @NonNull GeoReferenceAssertionService dinaService,    
    @NonNull BuildProperties props,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull ExternalResourceProvider externalResourceProvider,
    @NonNull AuditService auditService
  ) {
    super(
      dinaService,
      Optional.empty(),
      Optional.of(auditService),
      new DinaMapper<>(GeoreferenceAssertionDto.class),
      GeoreferenceAssertionDto.class,
      GeoreferenceAssertion.class,
      null,
      externalResourceProvider,
      props);

    this.authenticatedUser = authenticatedUser;
  }

  @Override
  public <S extends GeoreferenceAssertionDto> S create(S resource) {
    if (authenticatedUser.isPresent()) {
      resource.setCreatedBy(authenticatedUser.get().getUsername());
    }
    return super.create(resource);
  }
}

