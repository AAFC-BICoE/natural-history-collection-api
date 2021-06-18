package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.service.StorageUnitService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class StorageRepo extends DinaRepository<StorageUnitDto, StorageUnit> {
  public StorageRepo(
    @NonNull StorageUnitService sus,
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
  }

}
