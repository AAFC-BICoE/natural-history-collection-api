package ca.gc.aafc.collection.api.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.DinaComponent;
import ca.gc.aafc.collection.api.entities.ExtensionValue;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

import javax.inject.Inject;

public class BaseExtensionValueValidatorTest extends CollectionModuleBaseIT {
  
  @Inject
  private CollectingEventExtensionValueValidator collectingEventValidator;

  @Inject
  private RestrictionExtensionValueValidator restrictionValidator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_WhenValid_NoErrors() {
    ExtensionValue extensionValue = newCollectingEventExtensionValue();
    Errors errors = ValidationErrorsHelper.newErrorsObject(extensionValue.getExtKey(), extensionValue);
    collectingEventValidator.validate(extensionValue, errors);
    Assertions.assertFalse(errors.hasErrors());
  }

  @Test
  void validate_NoMatchTerm_HasErrors() {
    ExtensionValue extensionValue = newCollectingEventExtensionValue();
    extensionValue.setExtTerm("non-match");

    String expectedErrorMessage = getExpectedErrorMessage(
      CollectingEventExtensionValueValidator.NO_MATCH_TERM, 
      extensionValue.getExtKey(), 
      extensionValue.getExtVersion(),
      extensionValue.getExtTerm());

    Errors errors = ValidationErrorsHelper.newErrorsObject(extensionValue.getExtKey(), extensionValue);

    collectingEventValidator.validate(extensionValue, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_NoMatchKey_HasErrors() {
    ExtensionValue extensionValue = newCollectingEventExtensionValue();
    extensionValue.setExtKey("non-match");

    String expectedErrorMessage = getExpectedErrorMessage(
      CollectingEventExtensionValueValidator.NO_MATCH_KEY_VERSION, 
      extensionValue.getExtKey(), 
      extensionValue.getExtVersion());

    Errors errors = ValidationErrorsHelper.newErrorsObject(extensionValue.getExtKey(), extensionValue);

    collectingEventValidator.validate(extensionValue, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_IncorrectDinaComponentProvided_HasErrors() {
    ExtensionValue extensionValue = newRestrictionExtensionValue();

    String expectedErrorMessage = getExpectedErrorMessage(
      CollectingEventExtensionValueValidator.INCORRECT_DINA_COMPONENT, 
      DinaComponent.COLLECTING_EVENT, 
      DinaComponent.RESTRICTION);

    Errors errors = ValidationErrorsHelper.newErrorsObject(extensionValue.getExtKey(), extensionValue);

    // The collectingEvent is expecting a dina component of COLLECTING_EVENT. For this test, we
    // are providing a RESTRICTION extension type.
    collectingEventValidator.validate(extensionValue, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_NoMatchAcceptedValues_HasErrors() {
    ExtensionValue extensionValue = newRestrictionExtensionValue();
    extensionValue.setValue("non-match");

    String expectedErrorMessage = getExpectedErrorMessage(
      CollectingEventExtensionValueValidator.NO_MATCH_ACCEPTED_VALUE, 
      extensionValue.getExtKey(), 
      extensionValue.getExtVersion());

    Errors errors = ValidationErrorsHelper.newErrorsObject(extensionValue.getExtKey(), extensionValue);

    restrictionValidator.validate(extensionValue, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  private static ExtensionValue newCollectingEventExtensionValue() {
    return ExtensionValue.builder()
        .extKey("mixs_soil_v5")
        .extVersion("v5")
        .extTerm("experimental_factor")
        .value("definition of experimentWal factor")
        .build();
  }

  private static ExtensionValue newRestrictionExtensionValue() {
    return ExtensionValue.builder()
        .extKey("cfia_ppc")
        .extVersion("2022-02")
        .extTerm("level")
        .value("Level 2 (PPC-2)")
        .build();
  }

  private String getExpectedErrorMessage(String key, Object... objects) {
    return messageSource.getMessage(key, objects, LocaleContextHolder.getLocale());
  }
}
