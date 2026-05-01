package ca.gc.aafc.collection.api.service;

import jakarta.inject.Inject;
import jakarta.validation.ValidationException;

import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.validation.AssociationValidator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssociationServiceIT extends CollectionModuleBaseIT {

  private static final String INVALID_TYPE = "not a real type";

  @Inject
  private MessageSource messageSource;

  @Test
  void create_invalidAssociationType_exception() {

    MaterialSample sample = MaterialSampleFactory.newMaterialSample()
      .build();

    MaterialSample sample2 = MaterialSampleFactory.newMaterialSample()
      .build();

    materialSampleService.create(sample);
    materialSampleService.create(sample2);

    // Create association with invalid type.
    Association association = Association.builder()
      .createdBy("abc")
      .sample(sample)
      .associatedSample(sample2)
      .associationType(INVALID_TYPE)
      .build();

    // Expecting a validation exception with the ASSOCIATION_TYPE_NOT_IN_VOCABULARY message.
    String errorMessage = getExpectedErrorMessage(AssociationValidator.ASSOCIATION_TYPE_NOT_IN_VOCABULARY);
    ValidationException exception = assertThrows(ValidationException.class, () -> associationService.create(association));
    assertEquals(errorMessage, exception.getMessage());
  }

  private String getExpectedErrorMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
}
