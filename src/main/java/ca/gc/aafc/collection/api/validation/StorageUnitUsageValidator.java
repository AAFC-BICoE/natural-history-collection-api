package ca.gc.aafc.collection.api.validation;

import javax.inject.Named;
import lombok.NonNull;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.entities.StorageUnitUsage;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.dina.validation.AbstractStorageLocationValidator;

/**
 * Validates the well location of a {@link StorageUnitUsage}.
 */
@Component
public class StorageUnitUsageValidator extends AbstractStorageLocationValidator {

  static final String NO_STORAGE_UNIT_KEY = "validation.storageUnitUsage.noGridEnabledStorageType";
  static final String STORAGE_UNIT_OR_TYPE_KEY = "validation.storageUnitCoordinates.storageUnitOrType";

  private final MessageSource messageSource;

  public StorageUnitUsageValidator(@Named("validationMessageSource") MessageSource messageSourceFromBase,
                                   MessageSource messageSource) {
    super(messageSourceFromBase);
    this.messageSource = messageSource;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return StorageUnitUsage.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("StorageUnitUsageValidator not supported for class " + target.getClass());
    }

    StorageUnitUsage entity = (StorageUnitUsage) target;
    checkRowAndColumn(entity.getWellRow(), entity.getWellColumn(), errors);

    // this will trigger lazy-loading (unless hints are used when loading the material-sample)
    checkStorageOrTypeNotBoth(entity, errors);

    // if we have errors return since we won't be able to find the type in a reliable way
    if (errors.hasErrors()) {
      return;
    }

    StorageUnitType sut = entity.getEffectiveStorageUnitType();

    // make sure we have a storage unit type
    if (sut != null && sut.getGridLayoutDefinition() != null) {
      checkWellAgainstGrid(entity.getWellRow(), entity.getWellColumn(),
        sut.getGridLayoutDefinition(), errors);
    } else {
      if (entity.areCoordinatesSet()) {
        errors.rejectValue("storageUnitType", NO_STORAGE_UNIT_KEY,
          messageSource.getMessage(NO_STORAGE_UNIT_KEY, null, LocaleContextHolder.getLocale()));
      }
    }
  }

  private void checkStorageOrTypeNotBoth(StorageUnitUsage storageUnitCoordinates, Errors errors) {
    if (storageUnitCoordinates.getStorageUnit() == null && storageUnitCoordinates.getStorageUnitType() == null ||
      storageUnitCoordinates.getStorageUnit() != null && storageUnitCoordinates.getStorageUnitType() != null) {
      errors.rejectValue("storageUnitType", STORAGE_UNIT_OR_TYPE_KEY,
        messageSource.getMessage(STORAGE_UNIT_OR_TYPE_KEY, null, LocaleContextHolder.getLocale()));
    }
  }

}
