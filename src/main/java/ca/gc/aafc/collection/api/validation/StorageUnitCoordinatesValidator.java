package ca.gc.aafc.collection.api.validation;

import javax.inject.Named;
import lombok.NonNull;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.entities.StorageUnitCoordinates;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.dina.validation.AbstractStorageLocationValidator;

/**
 * Validates the well location of a {@link StorageUnitCoordinates}.
 */
@Component
public class StorageUnitCoordinatesValidator extends AbstractStorageLocationValidator {

  static final String NO_STORAGE_UNIT_KEY = "validation.materialSample.location.noStorageType";
  static final String STORAGE_UNIT_OR_TYPE_KEY = "validation.storageUnitCoordinates.storageUnitOrType";

  private final MessageSource messageSource;

  public StorageUnitCoordinatesValidator(@Named("validationMessageSource") MessageSource messageSourceFromBase,
                                         MessageSource messageSource) {
    super(messageSourceFromBase);
    this.messageSource = messageSource;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return StorageUnitCoordinates.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("StorageUnitCoordinatesValidator not supported for class " + target.getClass());
    }

    StorageUnitCoordinates entity = (StorageUnitCoordinates) target;
    checkRowAndColumn(entity.getWellRow(), entity.getWellColumn(), errors);

    // this will trigger lazy-loading (unless hints are used when loading the material-sample)
    checkStorageOrTypeNotBoth(entity, errors);

    // if we have errors return since we won't be able to find the type in a reliable way
    if (errors.hasErrors()) {
      return;
    }

    StorageUnitType sut = entity.getStorageUnitType() != null ? entity.getStorageUnitType() :
      entity.getStorageUnit().getStorageUnitType();

    // make sure we have a storage unit type
    if (sut != null && sut.getGridLayoutDefinition() != null) {
      checkWellAgainstGrid(entity.getWellRow(), entity.getWellColumn(),
        sut.getGridLayoutDefinition(), errors);
    } else {
      errors.rejectValue("storageUnit", "storageUnit.null",
        messageSource.getMessage(NO_STORAGE_UNIT_KEY, null, LocaleContextHolder.getLocale()));
    }
  }

  private void checkStorageOrTypeNotBoth(StorageUnitCoordinates storageUnitCoordinates, Errors errors) {
    if (storageUnitCoordinates.getStorageUnit() == null && storageUnitCoordinates.getStorageUnitType() == null ||
      storageUnitCoordinates.getStorageUnit() != null && storageUnitCoordinates.getStorageUnitType() != null) {
      errors.rejectValue("storageUnit", STORAGE_UNIT_OR_TYPE_KEY,
        messageSource.getMessage(STORAGE_UNIT_OR_TYPE_KEY, null, LocaleContextHolder.getLocale()));
    }
  }

}
