package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;
import java.util.UUID;

class MaterialSampleValidatorTest extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleValidator sampleValidator;

  @Test
  void validate_WhenValid_NoErrors() {
    MaterialSample sample = newSample();
    Errors errors = new BeanPropertyBindingResult(sample, "name");
    sampleValidator.validate(sample, errors);
    Assertions.assertFalse(errors.hasErrors());
  }

  @Test
  void validate_WhenParentIsSelf_HasError() {
    MaterialSample sample = newSample();
    sample.setParentMaterialSample(sample);
    Errors errors = new BeanPropertyBindingResult(sample, "name");
    sampleValidator.validate(sample, errors);
    Assertions.assertTrue(errors.hasErrors());
  }

  @Test
  void validate_WhenHasParentAndEvent_HasError() {
    MaterialSample sample = newSample();
    sample.setParentMaterialSample(newSample());
    sample.setCollectingEvent(CollectingEvent.builder().build());
    Errors errors = new BeanPropertyBindingResult(sample, "name");
    sampleValidator.validate(sample, errors);
    Assertions.assertTrue(errors.hasErrors());
  }

  private static MaterialSample newSample() {
    return MaterialSample.builder()
      .uuid(UUID.randomUUID())
      .group(RandomStringUtils.randomAlphabetic(3))
      .createdBy(RandomStringUtils.randomAlphabetic(3))
      .build();
  }
}