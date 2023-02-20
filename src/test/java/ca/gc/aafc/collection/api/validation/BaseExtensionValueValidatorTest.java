package ca.gc.aafc.collection.api.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.config.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.DinaComponent;
import ca.gc.aafc.collection.api.entities.ExtensionValue;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

import javax.inject.Inject;

public class BaseExtensionValueValidatorTest extends CollectionModuleBaseIT {
  
  private static final String COLLECTING_EVENT_KEY = "mixs_soil_v5";
  private static final String COLLECTING_EVENT_FIELD_KEY = "experimental_factor";
  private static final String COLLECTING_EVENT_VALUE = "definition of experimentWal factor";

  private static final String RESTRICTION_KEY = "cfia_ppc";
  private static final String RESTRICTION_FIELD_KEY = "level";
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
  void validate_onNoMatchFieldKey_hasErrors() {
    ExtensionValue extensionValue = newCollectingEventExtensionValue();
    extensionValue.setExtFieldKey(NON_MATCH);

    String expectedErrorMessage = getExpectedErrorMessage(
      CollectingEventExtensionValueValidator.NO_MATCH_FIELD_KEY,
      extensionValue.getExtKey(),
      extensionValue.getExtFieldKey());

    Errors errors = ValidationErrorsHelper.newErrorsObject(extensionValue.getExtKey(), extensionValue);

    collectingEventValidator.validate(extensionValue, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @ParameterizedTest
  @NullSource
  @ValueSource(strings = {"", " "})
  void validate_onBlankValue_hasErrors(String sourceValue) {
    ExtensionValue extensionValue = newCollectingEventExtensionValue();
    extensionValue.setValue(sourceValue);

    String expectedErrorMessage = getExpectedErrorMessage(
            CollectingEventExtensionValueValidator.BLANK_VALUE);

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
      extensionValue.getExtKey());

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
      String.join(", ", configuration.getExtension().get(RESTRICTION_KEY).getFieldByKey(RESTRICTION_FIELD_KEY).getAcceptedValues()));

    Errors errors = ValidationErrorsHelper.newErrorsObject(extensionValue.getExtKey(), extensionValue);

    restrictionValidator.validate(extensionValue, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  private static ExtensionValue newCollectingEventExtensionValue() {
    return ExtensionValue.builder()
        .extKey(COLLECTING_EVENT_KEY)
        .extFieldKey(COLLECTING_EVENT_FIELD_KEY)
        .value(COLLECTING_EVENT_VALUE)
        .build();
  }

  private static ExtensionValue newRestrictionExtensionValue() {
    return ExtensionValue.builder()
        .extKey(RESTRICTION_KEY)
        .extFieldKey(RESTRICTION_FIELD_KEY)
        .value(RESTRICTION_VALUE)
        .build();
  }

  private String getExpectedErrorMessage(String key, Object... objects) {
    return messageSource.getMessage(key, objects, LocaleContextHolder.getLocale());
  }
}
