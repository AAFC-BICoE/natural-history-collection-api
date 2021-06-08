package ca.gc.aafc.collection.api.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.ValidationException;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.context.MessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionManagedAttributeFactory;
import ca.gc.aafc.dina.entity.ManagedAttribute.ManagedAttributeType;

class CollectionManagedAttributeValueValidatorTest extends CollectionModuleBaseIT {

  @Inject
  private CollectionManagedAttributeValueValidator validatorUnderTest;

  @Named("validationMessageSource")
  private MessageSource messageSource;

  private CollectionManagedAttribute testManagedAttribute;

  @Test
  void validate_WhenValidStringType() {
    testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(new String[]{})
      .build();
    collectionManagedAttributeService.create(testManagedAttribute);

    Map<String, String> mav = Map.of(testManagedAttribute.getKey(), "new string value");

    Errors errors = new BeanPropertyBindingResult(mav, "mav");
    validatorUnderTest.validate(mav, errors);
    assertFalse(errors.hasErrors());
  }

  @Test
  void validate_WhenValidIntegerType() {
    testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(new String[]{})
      .managedAttributeType(ManagedAttributeType.INTEGER)
      .build();
    collectionManagedAttributeService.create(testManagedAttribute);

    Map<String, String> mav = Map.of(testManagedAttribute.getKey(), "1");

    Errors errors = new BeanPropertyBindingResult(mav, "mav");
    validatorUnderTest.validate(mav, errors);
    assertFalse(errors.hasErrors());
  }

  @ParameterizedTest
  @ValueSource(strings = {"1.2", "", "  ", "\t", "\n", "a"})
  void validate_WhenInvalidIntegerType(String value) {
    testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .managedAttributeType(ManagedAttributeType.INTEGER)
      .build();
    collectionManagedAttributeService.create(testManagedAttribute);

    Map<String, String> mav = Map.of(testManagedAttribute.getKey(), value);

    Errors errors = new BeanPropertyBindingResult(mav, "mav");
    validatorUnderTest.validate(mav, errors);
    assertTrue(errors.hasErrors());
  }

  @Test
  void validate_WhenInvalidIntegerTypeExceptionThrown() {
    testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .managedAttributeType(ManagedAttributeType.INTEGER)
      .build();
    collectionManagedAttributeService.create(testManagedAttribute);
    Map<String, String> mav = Map.of(testManagedAttribute.getKey(), "1.2");

    assertThrows(ValidationException.class, () -> validatorUnderTest.validate(CollectingEventFactory.newCollectingEvent().build(), mav));
  }

  @Test
  public void assignedValueContainedInAcceptedValues_validationPasses() {
    testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .name("My special Attribute")
      .acceptedValues(new String[]{"val1", "val2"})
      .build();
    
    collectionManagedAttributeService.create(testManagedAttribute);

    Map<String, String> mav = Map.of(testManagedAttribute.getKey(), "val1");

    Errors errors = new BeanPropertyBindingResult(mav, "mav");
    validatorUnderTest.validate(mav, errors);
    assertFalse(errors.hasErrors());
  }

  @Test
  public void assignedValueNotContainedInAcceptedValues_validationFails() {
    testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .name("key2")
      .acceptedValues(new String[]{"val1", "val2"})
      .build();
    
    collectionManagedAttributeService.create(testManagedAttribute);

    Map<String, String> mav = Map.of(testManagedAttribute.getKey(), "val3");

    Errors errors = new BeanPropertyBindingResult(mav, "mav");
    validatorUnderTest.validate(mav, errors);
    assertEquals(1, errors.getErrorCount());

    assertTrue(errors.getAllErrors().get(0).getCode().contains("val3"));
  }

  @Test
  public void assignedKeyDoesNotExist_validationFails() {
    Map<String, String> mav = Map.of("key_x", "val3");

    Errors errors = new BeanPropertyBindingResult(mav, "mav");
    validatorUnderTest.validate(mav, errors);
    assertEquals(1, errors.getErrorCount());

    assertTrue(errors.getAllErrors().get(0).getCode().contains("assignedValue key not found."));
  }

  @Test
  public void validate_whenEmpty_NoErrors() {
    Map<String, String> mav = Map.of();
    Errors errors = new BeanPropertyBindingResult(mav, "mav");
    validatorUnderTest.validate(mav, errors);
    assertFalse(errors.hasErrors());
  }

  @Test
  void validate_IllegalParameters() {
    String wrongType = "wrong type";
    Map<String, Integer> wrongValueType = Map.of("string", 2);
    Map<Integer, String> wrongKeyType = Map.of(2, "");
    assertThrows(
      IllegalArgumentException.class,
      () -> validatorUnderTest.validate(wrongType, new BeanPropertyBindingResult(wrongType, "mav")));
    assertThrows(
      IllegalArgumentException.class,
      () -> validatorUnderTest.validate(
        wrongValueType,
        new BeanPropertyBindingResult(wrongValueType, "mav")));
    assertThrows(
      IllegalArgumentException.class,
      () -> validatorUnderTest.validate(wrongKeyType, new BeanPropertyBindingResult(wrongKeyType, "mav")));
  }
  
}
