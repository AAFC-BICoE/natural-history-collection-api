package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.StorageUnitCoordinates;
import ca.gc.aafc.collection.api.validation.StorageUnitCoordinatesValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;

@Service
public class StorageUnitCoordinatesService extends DefaultDinaService<StorageUnitCoordinates> {

  private final StorageUnitCoordinatesValidator coordinatesValidator;

  public StorageUnitCoordinatesService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv, StorageUnitCoordinatesValidator coordinatesValidator) {
    super(baseDAO, sv);
    this.coordinatesValidator = coordinatesValidator;
  }

  @Override
  protected void preCreate(StorageUnitCoordinates entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
  }

  @Override
  public void validateBusinessRules(StorageUnitCoordinates entity) {
    applyBusinessRule(entity, coordinatesValidator);
  }

}
