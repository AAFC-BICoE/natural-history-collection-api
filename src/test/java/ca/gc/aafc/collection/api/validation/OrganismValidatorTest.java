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
import ca.gc.aafc.collection.api.testsupport.factories.DeterminationFactory;
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

    Determination determinationA = DeterminationFactory.newDetermination()
        .isPrimary(true)
        .build();

    Determination determinationB = DeterminationFactory.newDetermination()
        .isPrimary(true)
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
  void validate_WhenScientificNameAndVerbatimScientificNameIsNotSet_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(OrganismValidator.VALID_DETERMINATION_SCIENTIFICNAME);

    List<Determination> determinations = List.of(DeterminationFactory.newDetermination()
        .isPrimary(true)
        .scientificName(null)
        .verbatimScientificName(null)
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
    Determination determinationA = DeterminationFactory.newDetermination()
        .isPrimary(false)
        .build();

    Determination determinationB = DeterminationFactory.newDetermination()
        .isPrimary(false)
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

    Determination determinationA = DeterminationFactory.newDetermination()
        .isPrimary(true)
        .isFiledAs(true)
        .build();

    Determination determinationB = DeterminationFactory.newDetermination()
        .isPrimary(false)
        .isFiledAs(true)
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
  void validate_InvalidScientificSourceDetailsPair_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(OrganismValidator.INVALID_SCIENTIFICNAMESOURCE_DETAILS_PAIR);

    Determination determination = DeterminationFactory.newDetermination()
        .isPrimary(true)
        .scientificNameSource(null)
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
  void validate_MissingScientificSourceDetailsPair_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(OrganismValidator.MISSING_SCIENTIFICNAMESOURCE_DETAILS_PAIR);

    Determination determinationA = DeterminationFactory.newDetermination()
        .isPrimary(true)
        .verbatimScientificName(null)
        .scientificName("scientificName A")
        .scientificNameSource(null)
        .scientificNameDetails(ScientificNameSourceDetails.builder()
            .classificationPath("Poaceae|Poa")
            .classificationRanks("family|genus")
            .build()
        )
        .build();

    Determination determinationB = DeterminationFactory.newDetermination()
        .isPrimary(false)
        .verbatimScientificName(null)
        .scientificName("scientificName B")
        .scientificNameSource(ScientificNameSource.COLPLUS)
        .scientificNameDetails(null)
        .build();

    List<Determination> determinations = List.of(determinationA, determinationB);
    Organism organism = OrganismEntityFactory.newOrganism()
        .determination(determinations)
        .build();

    Errors errors = ValidationErrorsHelper.newErrorsObject(organism);
    organismValidator.validate(organism, errors);

    Assertions.assertTrue(errors.hasErrors());
    Assertions.assertEquals(2, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(1).getDefaultMessage());
  }

  @Test
  void validate_CustomClassificationWithSource_NoErrors() {
    // Attempt to set the scientific name details without a source provided.
    Determination determination = DeterminationFactory.newDetermination()
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

  @Test
  void validate_VerbatimScientificNameIsSetIncorrectSourceProvided_HasError() {
    String expectedErrorMessage = getExpectedErrorMessage(OrganismValidator.INVALID_SOURCE_PROVIDED);

    Determination determination = DeterminationFactory.newDetermination()
        .isPrimary(true)
        .scientificName(null)
        .scientificNameSource(ScientificNameSource.COLPLUS)
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

  private String getExpectedErrorMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
}
