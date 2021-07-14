package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.StorageUnit;
import lombok.NonNull;

@Component
public class StorageUnitValidator implements Validator {

  private final MessageSource messageSource;

  public static final String VALID_PARENT_RELATIONSHIP_LOOP = "validation.constraint.violation.loopingParentStorageUnit";

  public StorageUnitValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
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
    checkParentIsNotSelf(errors, storageUnit);
  }

  private void checkParentIsNotSelf(Errors errors, StorageUnit storageUnit) {
    if (storageUnit.getParentStorageUnit() != null
      && storageUnit.getParentStorageUnit().getUuid().equals(storageUnit.getUuid())) {
      String errorMessage = getMessage(VALID_PARENT_RELATIONSHIP_LOOP);
      errors.rejectValue("parentStorageUnit", VALID_PARENT_RELATIONSHIP_LOOP, errorMessage);
    }
  }

  private String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }

  
}
