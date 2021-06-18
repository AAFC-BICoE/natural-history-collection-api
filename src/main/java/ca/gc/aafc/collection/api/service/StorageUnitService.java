package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.jpa.OneToManyDinaService;
import ca.gc.aafc.dina.jpa.OneToManyFieldHandler;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.List;
import java.util.UUID;

@Service
public class StorageUnitService extends OneToManyDinaService<StorageUnit> {

  public StorageUnitService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv, List.of(
      new OneToManyFieldHandler<>(
        StorageUnit.class,
        storageUnit -> storageUnit::setParentStorageUnit,
        StorageUnit::getStorageUnitChildren,
        "parentStorageUnit",
        storageUnit -> storageUnit.setParentStorageUnit(null))
    ));
  }

  @Override
  protected void preCreate(StorageUnit entity) {
    entity.setUuid(UUID.randomUUID());
  }

}
