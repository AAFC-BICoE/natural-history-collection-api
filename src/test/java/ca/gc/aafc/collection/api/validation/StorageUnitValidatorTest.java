package ca.gc.aafc.collection.api.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitFactory;

public class StorageUnitValidatorTest extends CollectionModuleBaseIT {

  @Inject
  private StorageUnitValidator storageUnitValidator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_WhenValid_NoErrors() {
    StorageUnit storageUnit = StorageUnitFactory.newStorageUnit().build();
    Errors errors = new BeanPropertyBindingResult(storageUnit, storageUnit.getName());
    storageUnitValidator.validate(storageUnit, errors);
    assertFalse(errors.hasErrors());
  }

  @Test
  void validate_WhenParentIsSelf_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(StorageUnitValidator.VALID_PARENT_RELATIONSHIP_LOOP);
    StorageUnit storageUnit = StorageUnitFactory.newStorageUnit().build();
    storageUnit.setParentStorageUnit(storageUnit);
    Errors errors = new BeanPropertyBindingResult(storageUnit, storageUnit.getName());
    storageUnitValidator.validate(storageUnit, errors);
    assertTrue(errors.hasErrors());
    assertEquals(1, errors.getAllErrors().size());
    assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  private String getExpectedErrorMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
  
}
