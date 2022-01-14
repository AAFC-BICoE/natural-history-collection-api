package ca.gc.aafc.collection.api.validation;

import java.util.List;
import java.util.UUID;
import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Determination.ScientificNameSource;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleType;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

class MaterialSampleValidatorTest extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleValidator sampleValidator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_WhenValid_NoErrors() {
    MaterialSample sample = newSample();
    Errors errors = ValidationErrorsHelper.newErrorsObject(sample);
    sampleValidator.validate(sample, errors);
    Assertions.assertFalse(errors.hasErrors());
  }

  @Test
  void validate_WhenParentIsSelf_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(MaterialSampleValidator.VALID_PARENT_RELATIONSHIP_LOOP);
    MaterialSample sample = newSample();
    sample.setParentMaterialSample(sample);

    Errors errors = ValidationErrorsHelper.newErrorsObject(sample);

    sampleValidator.validate(sample, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_WhenHasParentAndEvent_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(MaterialSampleValidator.PARENT_AND_EVENT_ERROR_KEY);
    MaterialSample sample = newSample();
    sample.setParentMaterialSample(newSample());
    sample.setCollectingEvent(CollectingEvent.builder().build());
    Errors errors = ValidationErrorsHelper.newErrorsObject(sample);
    sampleValidator.validate(sample, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_WhenScientificNameSourceIsSetButScientificIsNotSet_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(MaterialSampleValidator.VALID_DETERMINATION_SCIENTIFICNAMESOURCE);

    Determination determination = Determination.builder()
      .isPrimary(true)
      .scientificNameSource(Determination.ScientificNameSource.COLPLUS)
      .verbatimScientificName("verbatimScientificName")
      .build();

    List<Determination> determinations = List.of(determination);

    MaterialSample sample = newSample();
    sample.setDetermination(determinations);

    Errors errors = ValidationErrorsHelper.newErrorsObject(sample);
    sampleValidator.validate(sample, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_WhenScientificNameAndVerbatimScientificNameIsNotSet_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(MaterialSampleValidator.VALID_DETERMINATION_SCIENTIFICNAME);

    List<Determination> determinations = List.of(Determination.builder()
      .isPrimary(true)
      .build());

    MaterialSample sample = newSample();
    sample.setDetermination(determinations);

    Errors errors = ValidationErrorsHelper.newErrorsObject(sample);
    sampleValidator.validate(sample, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_WhenNoPrimaryDeterminationWithMultipleDeterminations_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(MaterialSampleValidator.MISSING_PRIMARY_DETERMINATION);

    // Since there will be two determinations, the validatior can't automatically set one as primary.
    Determination determinationA = Determination.builder()
      .isPrimary(false)
      .verbatimScientificName("verbatimScientificNameA")
      .build();

    Determination determinationB = Determination.builder()
      .isPrimary(false)
      .verbatimScientificName("verbatimScientificNameB")
      .build();

    List<Determination> determinations = List.of(determinationA, determinationB);
    MaterialSample sample = newSample();
    sample.setDetermination(determinations);

    Errors errors = ValidationErrorsHelper.newErrorsObject(sample);
    sampleValidator.validate(sample, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_WhenNoPrimaryDeterminationWithOnlyOne_NoError() {
    // Since there is only one, the validator will automatically set it as the primary. No error expected.
    Determination determination = Determination.builder()
      .isPrimary(false)
      .verbatimScientificName("verbatimScientificNameA")
      .build();

    List<Determination> determinations = List.of(determination);
    MaterialSample sample = newSample();
    sample.setDetermination(determinations);

    Errors errors = ValidationErrorsHelper.newErrorsObject(sample);
    sampleValidator.validate(sample, errors);
    Assertions.assertFalse(errors.hasErrors());
  }

  @Test
  void validate_WhenNoPrimaryDeterminationAndMixedOrganism_NoErrors() {
    Determination determination = Determination.builder()
      .isPrimary(false)
      .verbatimScientificName("verbatimScientificName")
      .build();

    List<Determination> determinations = List.of(determination);
    MaterialSample sample = newSample();
    sample.setMaterialSampleType(MaterialSampleType.builder().uuid(MaterialSampleType.MIXED_ORGANISMS_UUID).build());
    sample.setDetermination(determinations);

    Errors errors = ValidationErrorsHelper.newErrorsObject(sample);
    sampleValidator.validate(sample, errors);
    Assertions.assertFalse(errors.hasErrors());
  }

  @Test
  void validate_WhenMoreThanOneIsFiledAs() {
    String expectedErrorMessage = getExpectedErrorMessage(MaterialSampleValidator.MORE_THAN_ONE_ISFILEDAS);

    Determination determinationA = Determination.builder()
      .isPrimary(true)
      .isFileAs(true)
      .verbatimScientificName("verbatimScientificName A")
      .scientificName("scientificName A")
      .scientificNameSource(ScientificNameSource.COLPLUS)
      .build();

    Determination determinationB = Determination.builder()
      .isPrimary(false)
      .isFileAs(true)
      .verbatimScientificName("verbatimScientificName B")
      .scientificName("scientificName B")
      .scientificNameSource(ScientificNameSource.COLPLUS)
      .build();

    List<Determination> determinations = List.of(determinationA, determinationB);
    MaterialSample sample = newSample();
    sample.setDetermination(determinations);

    Errors errors = ValidationErrorsHelper.newErrorsObject(sample);
    sampleValidator.validate(sample, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_WhenMoreThanOneIsPrimary() {
    String expectedErrorMessageNonMixed = getExpectedErrorMessage(MaterialSampleValidator.MISSING_PRIMARY_DETERMINATION);
    String expectedErrorMessageMixed = getExpectedErrorMessage(MaterialSampleValidator.MISSING_PRIMARY_DETERMINATION_MIXED_ORGANISM);

    Determination determinationA = Determination.builder()
      .isPrimary(true)
      .isFileAs(false)
      .verbatimScientificName("verbatimScientificName A")
      .scientificName("scientificName A")
      .scientificNameSource(ScientificNameSource.COLPLUS)
      .build();

    Determination determinationB = Determination.builder()
      .isPrimary(true)
      .isFileAs(false)
      .verbatimScientificName("verbatimScientificName B")
      .scientificName("scientificName B")
      .scientificNameSource(ScientificNameSource.COLPLUS)
      .build();

    List<Determination> determinations = List.of(determinationA, determinationB);

    MaterialSample sample = newSample();
    sample.setDetermination(determinations);

    Errors errorsNonMixedSpecimen = ValidationErrorsHelper.newErrorsObject(sample);
    sampleValidator.validate(sample, errorsNonMixedSpecimen);
    Assertions.assertTrue(errorsNonMixedSpecimen.hasErrors());
    Assertions.assertEquals(1, errorsNonMixedSpecimen.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessageNonMixed, errorsNonMixedSpecimen.getAllErrors().get(0).getDefaultMessage());

    // Try it again but Material Sample is now a Mixed Organism, expecting the same errors.
    sample.setMaterialSampleType(MaterialSampleType.builder().uuid(MaterialSampleType.MIXED_ORGANISMS_UUID).build());

    Errors errorsMixedSpecimen = ValidationErrorsHelper.newErrorsObject(sample);
    sampleValidator.validate(sample, errorsMixedSpecimen);
    Assertions.assertTrue(errorsMixedSpecimen.hasErrors());
    Assertions.assertEquals(1, errorsMixedSpecimen.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessageMixed, errorsMixedSpecimen.getAllErrors().get(0).getDefaultMessage());
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
