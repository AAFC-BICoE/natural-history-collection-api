package ca.gc.aafc.collection.api.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.ExtensionValue;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

import javax.inject.Inject;

public class ExtensionValueValidatorTest extends CollectionModuleBaseIT {
  
  @Inject
  private ExtensionValueValidator validator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_WhenValid_NoErrors() {
    ExtensionValue extensionValue = newExtensionValue();
    Errors errors = ValidationErrorsHelper.newErrorsObject(extensionValue.getExtKey(), extensionValue);
    validator.validate(extensionValue, errors);
    Assertions.assertFalse(errors.hasErrors());
  }

  @Test
  void validate_NoMatchTerm_HasErrors() {
    ExtensionValue extensionValue = newExtensionValue();
    extensionValue.setExtTerm("non-match");

    String expectedErrorMessage = getExpectedErrorMessage(
      ExtensionValueValidator.NO_MATCH_TERM, 
      extensionValue.getExtKey(), 
      extensionValue.getExtVersion(),
      extensionValue.getExtTerm());

    Errors errors = ValidationErrorsHelper.newErrorsObject(extensionValue.getExtKey(), extensionValue);

    validator.validate(extensionValue, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_NoMatchKey_HasErrors() {
    ExtensionValue extensionValue = newExtensionValue();
    extensionValue.setExtKey("non-match");

    String expectedErrorMessage = getExpectedErrorMessage(
      ExtensionValueValidator.NO_MATCH_KEY_VERSION, 
      extensionValue.getExtKey(), 
      extensionValue.getExtVersion());

    Errors errors = ValidationErrorsHelper.newErrorsObject(extensionValue.getExtKey(), extensionValue);

    validator.validate(extensionValue, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage();
  }

  private static ExtensionValue newExtensionValue() {
    return ExtensionValue.builder()
    .extKey("mixs_soil_v5")
    .extVersion("v5")
    .extTerm("experimental_factor")
    .value("definition of experimentWal factor")
    .build();
  }

  private String getExpectedErrorMessage(String key, Object... objects) {
    return messageSource.getMessage(key, objects, LocaleContextHolder.getLocale());
  }
}
