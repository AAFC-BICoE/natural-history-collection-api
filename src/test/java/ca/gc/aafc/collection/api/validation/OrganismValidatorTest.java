package ca.gc.aafc.collection.api.validation;

import java.util.List;
import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Determination.ScientificNameSource;
import ca.gc.aafc.collection.api.entities.Determination.ScientificNameSourceDetails;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

public class OrganismValidatorTest extends CollectionModuleBaseIT {

  @Inject
  private OrganismValidator organismValidator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_WhenMoreThanOneIsPrimary_HasError() {
    String expectedErrorMessageNonMixed = getExpectedErrorMessage(OrganismValidator.MISSING_PRIMARY_DETERMINATION);

    Determination determinationA = Determination.builder()
        .isPrimary(true)
        .isFileAs(false)
        .verbatimScientificName("verbatimScientificName A")
        .scientificName("scientificName A")
        .scientificNameSource(Determination.ScientificNameSource.COLPLUS)
        .build();

    Determination determinationB = Determination.builder()
        .isPrimary(true)
        .isFileAs(false)
        .verbatimScientificName("verbatimScientificName B")
        .scientificName("scientificName B")
        .scientificNameSource(Determination.ScientificNameSource.COLPLUS)
        .build();

    List<Determination> determinations = List.of(determinationA, determinationB);
    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(determinations)
        .build();

    Errors errors = ValidationErrorsHelper.newErrorsObject(organism);
    organismValidator.validate(organism, errors);

    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessageNonMixed, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_WhenScientificNameSourceIsSetButScientificIsNotSet_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(OrganismValidator.VALID_DETERMINATION_SCIENTIFICNAMESOURCE);

    Determination determination = Determination.builder()
        .isPrimary(true)
        .scientificNameSource(Determination.ScientificNameSource.COLPLUS)
        .verbatimScientificName("verbatimScientificName")
        .build();

    List<Determination> determinations = List.of(determination);
    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(determinations)
        .build();

    Errors errors = ValidationErrorsHelper.newErrorsObject(organism);
    organismValidator.validate(organism, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_WhenScientificNameAndVerbatimScientificNameIsNotSet_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(OrganismValidator.VALID_DETERMINATION_SCIENTIFICNAME);

    List<Determination> determinations = List.of(Determination.builder()
        .isPrimary(true)
        .build());

    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(determinations)
        .build();

    Errors errors = ValidationErrorsHelper.newErrorsObject(organism);
    organismValidator.validate(organism, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_WhenNoPrimaryDeterminationWithMultipleDeterminations_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(OrganismValidator.MISSING_PRIMARY_DETERMINATION);

    // Since there will be two determinations, we can't automatically set one as primary.
    Determination determinationA = Determination.builder()
        .isPrimary(false)
        .verbatimScientificName("verbatimScientificNameA")
        .build();

    Determination determinationB = Determination.builder()
        .isPrimary(false)
        .verbatimScientificName("verbatimScientificNameB")
        .build();

    List<Determination> determinations = List.of(determinationA, determinationB);
    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(determinations)
        .build();

    Errors errors = ValidationErrorsHelper.newErrorsObject(organism);
    organismValidator.validate(organism, errors);
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_WhenMoreThanOneIsFiledAs_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(OrganismValidator.MORE_THAN_ONE_ISFILEDAS);

    Determination determinationA = Determination.builder()
        .isPrimary(true)
        .isFileAs(true)
        .verbatimScientificName("verbatimScientificName A")
        .scientificName("scientificName A")
        .scientificNameSource(Determination.ScientificNameSource.COLPLUS)
        .build();

    Determination determinationB = Determination.builder()
        .isPrimary(false)
        .isFileAs(true)
        .verbatimScientificName("verbatimScientificName B")
        .scientificName("scientificName B")
        .scientificNameSource(Determination.ScientificNameSource.COLPLUS)
        .build();

    List<Determination> determinations = List.of(determinationA, determinationB);
    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(determinations)
        .build();

    Errors errors = ValidationErrorsHelper.newErrorsObject(organism);
    organismValidator.validate(organism, errors);

    // Expect 2 errors, since 2 of the organisms have invalid determinations
    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_customClassificationWithoutSource_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(OrganismValidator.MISSING_SCIENTIFICNAMESOURCE);

    // Attempt to set the verbatim scientific name details without a source provided.
    Determination determination = Determination.builder()
        .isPrimary(true)
        .verbatimScientificName("verbatimScientificName A")
        .scientificNameDetails(ScientificNameSourceDetails.builder()
            .classificationPath("Poaceae|Poa")
            .classificationRanks("family|genus")
            .build()
        )
        .build();

    List<Determination> determinations = List.of(determination);
    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(determinations)
        .build();

    Errors errors = ValidationErrorsHelper.newErrorsObject(organism);
    organismValidator.validate(organism, errors);

    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }


  @Test
  void validate_customClassificationWithSource_NoErrors() {
    // Attempt to set the scientific name details without a source provided.
    Determination determination = Determination.builder()
        .isPrimary(true)
        .scientificNameSource(ScientificNameSource.CUSTOM)
        .verbatimScientificName("verbatimScientificName A")
        .scientificNameDetails(ScientificNameSourceDetails.builder()
            .classificationPath("Poaceae|Poa")
            .classificationRanks("family|genus")
            .build()
        )
        .build();

    List<Determination> determinations = List.of(determination);
    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(determinations)
        .build();

    Errors errors = ValidationErrorsHelper.newErrorsObject(organism);
    organismValidator.validate(organism, errors);

    Assertions.assertFalse(errors.hasErrors());
  }

  private String getExpectedErrorMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
}
