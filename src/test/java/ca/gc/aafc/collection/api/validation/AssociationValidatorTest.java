package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;
import org.apache.commons.lang3.RandomStringUtils;

import jakarta.inject.Inject;
import jakarta.validation.ValidationException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AssociationValidatorTest extends CollectionModuleBaseIT {
  
  @Inject
  private AssociationValidator associationValidator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_WhenValid_NoErrors() {
    Association association = newAssociation();
    Errors errors = ValidationErrorsHelper.newErrorsObject(association.getAssociationType(), association);
    associationValidator.validate(association, association.getAssociationType());
    Assertions.assertFalse(errors.hasErrors());
  }

  @Test
  void validate_WhenAssociatedWithSelf_HasError() {
    Association association = newAssociation();
    MaterialSample sample = newSample();
    association.setAssociatedSample(sample);
    association.setSample(sample);

    ValidationException ex = assertThrows(ValidationException.class, () -> associationValidator.validate(association, association.getAssociationType()));
    Assertions.assertTrue(ex.getMessage().contains("between the same sample"));
  }

  @Test
  void validate_WhenAssociationTypeNotValid_HasError() {
    Association association = newAssociation();
    association.setAssociationType("invalid_associationType");

    ValidationException ex = assertThrows(ValidationException.class, () -> associationValidator.validate(association, association.getAssociationType()));
    Assertions.assertTrue(ex.getMessage().contains("unknown controlled vocabulary key"));
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
