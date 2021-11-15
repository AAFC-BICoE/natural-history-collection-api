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
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

public class CollectionValidatorTest extends CollectionModuleBaseIT {
  
  @Inject
  private CollectionValidator collectionValidator;

  @Inject MessageSource messageSource;

  @Test
  void validate_WhenValid_NoErrors() {
    Collection collection = newCollection();
    Errors errors = ValidationErrorsHelper.newErrorsObject(collection);
    collectionValidator.validate(collection, errors);
    Assertions.assertFalse(errors.hasErrors());
  }

  @Test
  void validate_ParentHasParent_HasErrors() {
    String expectedErrorMessage = getExpectedErrorMessage(CollectionValidator.VALID_PARENT_HAS_NO_PARENT);

    Collection collection = newCollection();
    Collection parentCollection = newCollection();
    Collection parentParentCollection = newCollection();
    collection.setParentCollection(parentCollection);
    parentCollection.setParentCollection(parentParentCollection);

    Errors errors = ValidationErrorsHelper.newErrorsObject(collection);

    collectionValidator.validate(collection, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  private static Collection newCollection() {
    return Collection.builder()
      .uuid(UUID.randomUUID())
      .group(RandomStringUtils.randomAlphabetic(3))
      .createdBy(RandomStringUtils.randomAlphabetic(3))
      .build();
  }

  private String getExpectedErrorMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
}
