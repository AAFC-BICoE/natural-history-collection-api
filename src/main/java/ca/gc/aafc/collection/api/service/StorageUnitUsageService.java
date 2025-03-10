package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.StorageUnitUsage;
import ca.gc.aafc.collection.api.validation.StorageUnitUsageValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;

@Service
public class StorageUnitUsageService extends DefaultDinaService<StorageUnitUsage> {

  private final StorageUnitUsageValidator storageUnitUsageValidator;

  public StorageUnitUsageService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv, StorageUnitUsageValidator storageUnitUsageValidator) {
    super(baseDAO, sv);
    this.storageUnitUsageValidator = storageUnitUsageValidator;
  }

  @Override
  protected void preCreate(StorageUnitUsage entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
    entity.setGroup(standardizeGroupName(entity));
  }

  @Override
  public void validateBusinessRules(StorageUnitUsage entity) {
    applyBusinessRule(entity, storageUnitUsageValidator);
  }
}
