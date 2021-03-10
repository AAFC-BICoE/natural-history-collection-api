package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.CollectorGroupDto;
import ca.gc.aafc.collection.api.entities.CollectorGroup;
import ca.gc.aafc.collection.api.service.CollectorGroupService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class CollectorGroupRepository extends DinaRepository<CollectorGroupDto, CollectorGroup> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;

  public CollectorGroupRepository(
    @NonNull CollectorGroupService dinaService,
    @NonNull BuildProperties props,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser,
    @NonNull ExternalResourceProvider externalResourceProvider
  ) {
    super(
      dinaService,
      Optional.empty(),
      Optional.empty(),
      new DinaMapper<>(CollectorGroupDto.class),
      CollectorGroupDto.class,
      CollectorGroup.class,
      null,
      externalResourceProvider,
      props);
      this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends CollectorGroupDto> S create(S resource) {
    if (dinaAuthenticatedUser.isPresent()) {
      resource.setCreatedBy(dinaAuthenticatedUser.get().getUsername());
    }
    return super.create(resource);
  }
}
