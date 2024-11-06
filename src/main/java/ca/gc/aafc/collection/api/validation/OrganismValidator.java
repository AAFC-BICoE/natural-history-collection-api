package ca.gc.aafc.collection.api.validation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Organism;
import ca.gc.aafc.dina.validation.DinaBaseValidator;

@Component
public class OrganismValidator extends DinaBaseValidator<Organism> {

  public static final String VALID_DETERMINATION_SCIENTIFICNAME = "validation.constraint.violation.determination.scientificname";
  public static final String MISSING_PRIMARY_DETERMINATION = "validation.constraint.violation.determination.primaryDeterminationMissing";
  public static final String MORE_THAN_ONE_ISFILEDAS = "validation.constraint.violation.determination.moreThanOneIsFiledAs";
  public static final String MISSING_SCIENTIFICNAMESOURCE_DETAILS_PAIR = "validation.constraint.violation.determination.scientificNameSourceOrDetailsMissing";
  public static final String SOURCE_REQUIRES_SCIENTIFIC_NAME = "validation.constraint.violation.determination.sourceRequiresScientificName";

  public OrganismValidator(MessageSource messageSource) {
    super(Organism.class, messageSource);
  }

  @Override
  public void validateTarget(Organism target, Errors errors) {
    checkDetermination(target, errors);
  }

  private void checkDetermination(Organism organism, Errors errors) {

    if (CollectionUtils.isNotEmpty(organism.getDetermination())) {

      // Ensure there is always 1 primary determination
      if (organism.countPrimaryDetermination() != 1) {
        rejectValueWithMessage("determination", errors, MISSING_PRIMARY_DETERMINATION);
      }

      // Check there is 0 or 1 isFiledAs but never more
      if (organism.countFiledAsDetermination() > 1) {
        rejectValueWithMessage("determination", errors, MORE_THAN_ONE_ISFILEDAS);
      }

      for (Determination determination : organism.getDetermination()) {

        // One of the scientific name fields have to provided for a determination.
        if (StringUtils.isBlank(determination.getVerbatimScientificName()) &&
            StringUtils.isBlank(determination.getScientificName())) {
          rejectValueWithMessage("determination", errors, VALID_DETERMINATION_SCIENTIFICNAME);
        }

        // If we have an external scientificNameSource (not CUSTOM or null)
        if (determination.isExternalScientificNameSource()) {

          if (StringUtils.isBlank(determination.getScientificName())) {
            rejectValueWithMessage("determination", errors, SOURCE_REQUIRES_SCIENTIFIC_NAME);
          }

          if (determination.getScientificNameDetails() == null) {
            rejectValueWithMessage("determination", errors,
              MISSING_SCIENTIFICNAMESOURCE_DETAILS_PAIR);
          }
        }
      }
    }
  }

  private void rejectValueWithMessage(String field, Errors errors, String messageKey) {
    errors.rejectValue(field, messageKey, getMessage(messageKey));
  }
}
