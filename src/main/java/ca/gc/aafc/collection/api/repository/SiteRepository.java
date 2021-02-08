package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.collection.api.service.SiteService;
import ca.gc.aafc.dina.filter.DinaFilterResolver;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;

import java.util.Optional;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

@Repository
public class SiteRepository extends DinaRepository<SiteDto, Site> {

  private Optional<DinaAuthenticatedUser> authenticatedUser;

  public SiteRepository(
    @NonNull SiteService dinaService,
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
      new DinaMapper<>(SiteDto.class),
      SiteDto.class,
      Site.class,
      filterResolver,
      externalResourceProvider,
      props);

    this.authenticatedUser = authenticatedUser;
  }

  @Override
  public <S extends SiteDto> S create(S resource) {
    if (authenticatedUser.isPresent()) {
      resource.setCreatedBy(authenticatedUser.get().getUsername());
    }
    return super.create(resource);
  }
}

