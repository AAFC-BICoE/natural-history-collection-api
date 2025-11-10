package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.entities.StorageUnitUsage;
import ca.gc.aafc.collection.api.validation.StorageUnitUsageValidator;
import ca.gc.aafc.dina.entity.StorageGridLayout;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.translator.NumberLetterTranslator;
import ca.gc.aafc.dina.util.UUIDHelper;

import java.util.List;
import java.util.Map;
import lombok.NonNull;

@Service
public class StorageUnitUsageService extends DefaultDinaService<StorageUnitUsage> {

  private final StorageUnitUsageValidator storageUnitUsageValidator;

  public StorageUnitUsageService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv, StorageUnitUsageValidator storageUnitUsageValidator) {
    super(baseDAO, sv);
    this.storageUnitUsageValidator = storageUnitUsageValidator;
  }

  @Override
  public StorageUnitUsage handleOptionalFields(StorageUnitUsage entity, Map<String, List<String>> optionalFields) {
    if (optionalFields.getOrDefault(StorageUnitUsageDto.TYPENAME, List.of()).contains(StorageUnitUsage.CELL_NUMBER_PROP_NAME)) {
      setCellNumber(entity);
    }
    return entity;
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

  /**
   * Calculated cell number (if possible to compute)
   * @param entity
   */
  private void setCellNumber(StorageUnitUsage entity) {
    StorageUnitType sut = entity.getEffectiveStorageUnitType();
    if (sut == null || sut.getGridLayoutDefinition() == null) {
      entity.setCellNumber(null);
      return;
    }

    if (!entity.areCoordinatesSet()) {
      entity.setCellNumber(null);
      return;
    }

    StorageGridLayout restriction = sut.getGridLayoutDefinition();
    entity.setCellNumber(restriction.calculateCellNumber(NumberLetterTranslator.toNumber(entity.getWellRow()),
      entity.getWellColumn()));
  }
}
