package ca.gc.aafc.collection.api.validation;

import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;
import groovy.xml.StreamingDOMBuilder._closure1;

public class AssociationValidatorTest extends CollectionModuleBaseIT {
  
  @Inject
  private AssociationValidator associationValidator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_WhenValid_NoErrors() {
    Association association = newAssociation();
    Errors errors = ValidationErrorsHelper.newErrorsObject(association.getAssociationType(), association);
    associationValidator.validate(association, errors);
    Assertions.assertFalse(errors.hasErrors());
  }

  @Test
  void validate_WhenAssociationTypeNotValid_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(AssociationValidator.ASSOCIATION_TYPE_NOT_IN_VOCABULARY);

    Association association = newAssociation();
    association.setAssociationType("invalid_associationType");

    Errors errors = ValidationErrorsHelper.newErrorsObject(association.getAssociationType(), association);

    associationValidator.validate(association, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  private static Association newAssociation() {
    return Association.builder()
      .associatedSample(newSample())
      .sample(newSample())
      .remarks(RandomStringUtils.randomAlphabetic(3))
      .associationType("host_of")
      .build();
  }

  private static MaterialSample newSample() {
    return MaterialSample.builder()
      .uuid(UUID.randomUUID())
      .group(RandomStringUtils.randomAlphabetic(3))
      .createdBy(RandomStringUtils.randomAlphabetic(3))
      .build();
  }

  private String getExpectedErrorMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }

}