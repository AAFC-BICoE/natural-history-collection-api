package ca.gc.aafc.collection.api.repository;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.GeoReferenceAssertionDto;
import ca.gc.aafc.collection.api.entities.GeoReferenceAssertion;
import ca.gc.aafc.collection.api.service.GeoReferenceAssertionService;
import ca.gc.aafc.dina.filter.DinaFilterResolver;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;

@Repository
public class GeoReferenceAssertionRepository extends DinaRepository<GeoReferenceAssertionDto, GeoReferenceAssertion> {

  private Optional<DinaAuthenticatedUser> authenticatedUser;  

    public GeoReferenceAssertionRepository(
    @NonNull GeoReferenceAssertionService dinaService,    
    @NonNull BaseDAO baseDAO,
    @NonNull DinaFilterResolver filterResolver,
    @NonNull BuildProperties props,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull ExternalResourceProvider externalResourceProvider
  ) {
    super(
      dinaService,
      Optional.empty(),
      Optional.empty(),
      new DinaMapper<>(GeoReferenceAssertionDto.class),
      GeoReferenceAssertionDto.class,
      GeoReferenceAssertion.class,
      filterResolver,
      externalResourceProvider,
      props);

    this.authenticatedUser = authenticatedUser;
  }

  @Override
  public <S extends GeoReferenceAssertionDto> S create(S resource) {
    if (authenticatedUser.isPresent()) {
      resource.setCreatedBy(authenticatedUser.get().getUsername());
    }
    return super.create(resource);
  }
}

