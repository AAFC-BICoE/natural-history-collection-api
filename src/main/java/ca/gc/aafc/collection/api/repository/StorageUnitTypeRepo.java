package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.service.StorageUnitTypeService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StorageUnitTypeRepo extends DinaRepository<StorageUnitTypeDto, StorageUnitType> {

  private Optional<DinaAuthenticatedUser> authenticatedUser;

  public StorageUnitTypeRepo(
    @NonNull StorageUnitTypeService service,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull BuildProperties buildProperties
  ) {
    super(
      service,
      Optional.empty(),
      Optional.empty(),
      new DinaMapper<>(StorageUnitTypeDto.class),
      StorageUnitTypeDto.class,
      StorageUnitType.class,
      null,
      null,
      buildProperties);
    this.authenticatedUser = authenticatedUser;

  }

  @Override
  public <S extends StorageUnitTypeDto> S create(S resource) {
    authenticatedUser.ifPresent(user -> resource.setCreatedBy(user.getUsername()));
    return super.create(resource);
  }
}