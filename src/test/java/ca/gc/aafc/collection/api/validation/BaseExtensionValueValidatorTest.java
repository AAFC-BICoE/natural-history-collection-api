package ca.gc.aafc.collection.api.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.DinaComponent;
import ca.gc.aafc.collection.api.entities.ExtensionValue;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

import javax.inject.Inject;

public class BaseExtensionValueValidatorTest extends CollectionModuleBaseIT {
  
  private static final String COLLECTING_EVENT_KEY = "mixs_soil_v5";
  private static final String COLLECTING_EVENT_VERSION = "v5";
  private static final String COLLECTING_EVENT_TERM = "experimental_factor";
  private static final String COLLECTING_EVENT_VALUE = "definition of experimentWal factor";

  private static final String RESTRICTION_KEY = "cfia_ppc";
  private static final String RESTRICTION_VERSION = "2022-02";
  private static final String RESTRICTION_TERM = "level";
  private static final String RESTRICTION_VALUE = "Level 2 (PPC-2)";

  private static final String NON_MATCH = "non-match";

  @Inject
  private CollectingEventExtensionValueValidator collectingEventValidator;

  @Inject
  private RestrictionExtensionValueValidator restrictionValidator;

  @Inject
  private CollectionExtensionConfiguration configuration;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_WhenValid_NoErrors() {
    ExtensionValue collectionExtensionValue = newCollectingEventExtensionValue();

    Errors collectionErrors = ValidationErrorsHelper.newErrorsObject(collectionExtensionValue.getExtKey(), collectionExtensionValue);
    collectingEventValidator.validate(collectionExtensionValue, collectionErrors);
    Assertions.assertFalse(collectionErrors.hasErrors());

    ExtensionValue restrictionExtensionValue = newCollectingEventExtensionValue();
    
    Errors restrictionErrors = ValidationErrorsHelper.newErrorsObject(restrictionExtensionValue.getExtKey(), restrictionExtensionValue);
    collectingEventValidator.validate(restrictionExtensionValue, restrictionErrors);
    Assertions.assertFalse(restrictionErrors.hasErrors());
  }

  @Test
  void validate_NoMatchTerm_HasErrors() {
    ExtensionValue extensionValue = newCollectingEventExtensionValue();
    extensionValue.setExtTerm(NON_MATCH);

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
    extensionValue.setExtKey(NON_MATCH);

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
    extensionValue.setValue(NON_MATCH);

    String expectedErrorMessage = getExpectedErrorMessage(
      RestrictionExtensionValueValidator.NO_MATCH_ACCEPTED_VALUE, 
      extensionValue.getValue(), 
      String.join(", ", configuration.getExtension().get(RESTRICTION_KEY).getFieldByTerm(RESTRICTION_TERM).getAcceptedValues()));

    Errors errors = ValidationErrorsHelper.newErrorsObject(extensionValue.getExtKey(), extensionValue);

    restrictionValidator.validate(extensionValue, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  private static ExtensionValue newCollectingEventExtensionValue() {
    return ExtensionValue.builder()
        .extKey(COLLECTING_EVENT_KEY)
        .extVersion(COLLECTING_EVENT_VERSION)
        .extTerm(COLLECTING_EVENT_TERM)
        .value(COLLECTING_EVENT_VALUE)
        .build();
  }

  private static ExtensionValue newRestrictionExtensionValue() {
    return ExtensionValue.builder()
        .extKey(RESTRICTION_KEY)
        .extVersion(RESTRICTION_VERSION)
        .extTerm(RESTRICTION_TERM)
        .value(RESTRICTION_VALUE)
        .build();
  }

  private String getExpectedErrorMessage(String key, Object... objects) {
    return messageSource.getMessage(key, objects, LocaleContextHolder.getLocale());
  }
}