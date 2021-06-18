package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.service.StorageUnitService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StorageRepo extends DinaRepository<StorageUnitDto, StorageUnit> {

  private Optional<DinaAuthenticatedUser> authenticatedUser;

  public StorageRepo(
    @NonNull StorageUnitService sus,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties buildProperties
  ) {
    super(
      sus,
      Optional.empty(),
      Optional.empty(),
      new DinaMapper<>(StorageUnitDto.class),
      StorageUnitDto.class,
      StorageUnit.class,
      null,
      null,
      buildProperties);
    this.authenticatedUser = authenticatedUser;
  }

  @Override
  public <S extends StorageUnitDto> S create(S resource) {
    authenticatedUser.ifPresent(user -> resource.setCreatedBy(user.getUsername()));
    return super.create(resource);
  }

}
