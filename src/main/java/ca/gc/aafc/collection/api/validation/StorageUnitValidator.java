package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.dina.service.HierarchicalObject;
import ca.gc.aafc.dina.service.PostgresHierarchicalDataService;

import java.util.List;
import java.util.UUID;

import lombok.NonNull;

@Component
public class StorageUnitValidator implements Validator {

  private final PostgresHierarchicalDataService postgresHierarchicalDataService;
  private final MessageSource messageSource;

  public static final String VALID_PARENT_RELATIONSHIP_LOOP = "validation.constraint.violation.loopingParentStorageUnit";

  public StorageUnitValidator(
    MessageSource messageSource,
    PostgresHierarchicalDataService postgresHierarchicalDataService
  ) {
    this.messageSource = messageSource;
    this.postgresHierarchicalDataService = postgresHierarchicalDataService;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return StorageUnit.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("StorageUnitValidator not supported for class " + target.getClass());
    }

    StorageUnit storageUnit = (StorageUnit) target;
    // checkParentIsNotSelf(errors, storageUnit);
    checkParentIsNotInHierarchy(errors, storageUnit);
  }

  private void checkParentIsNotInHierarchy(Errors errors, StorageUnit storageUnit) {
    List<HierarchicalObject> hierarchicalObjects = postgresHierarchicalDataService.getHierarchy(
      storageUnit.getId(),
      StorageUnit.TABLE_NAME,
      StorageUnit.ID_COLUMN_NAME,
      StorageUnit.UUID_COLUMN_NAME,
      StorageUnit.PARENT_ID_COLUMN_NAME,
      StorageUnit.NAME_COLUMN_NAME);
    UUID parentUuid = storageUnit.getParentStorageUnit().getUuid();
    for (HierarchicalObject hierarchicalObject : hierarchicalObjects) {
      if (hierarchicalObject.getUuid().equals(parentUuid)) {
        String errorMessage = getMessage(VALID_PARENT_RELATIONSHIP_LOOP);
        errors.rejectValue("parentStorageUnit", VALID_PARENT_RELATIONSHIP_LOOP, errorMessage);
      }
    }
  }

  // private void checkParentIsNotSelf(Errors errors, StorageUnit storageUnit) {
  //   if (storageUnit.getParentStorageUnit() != null
  //     && storageUnit.getParentStorageUnit().getUuid().equals(storageUnit.getUuid())) {
  //     String errorMessage = getMessage(VALID_PARENT_RELATIONSHIP_LOOP);
  //     errors.rejectValue("parentStorageUnit", VALID_PARENT_RELATIONSHIP_LOOP, errorMessage);
  //   }
  // }

  private String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }

  
}
