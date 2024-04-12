package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.Organism;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class OrganismValidator implements Validator {

  public static final String VALID_DETERMINATION_SCIENTIFICNAME = "validation.constraint.violation.determination.scientificname";
  public static final String MISSING_PRIMARY_DETERMINATION = "validation.constraint.violation.determination.primaryDeterminationMissing";
  public static final String MORE_THAN_ONE_ISFILEDAS = "validation.constraint.violation.determination.moreThanOneIsFiledAs";
  public static final String MISSING_SCIENTIFICNAMESOURCE_DETAILS_PAIR = "validation.constraint.violation.determination.scientificNameSourceOrDetailsMissing";
  public static final String SOURCE_REQUIRES_SCIENTIFIC_NAME = "validation.constraint.violation.determination.sourceRequiresScientificName";

  private final MessageSource messageSource;

  public OrganismValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return Organism.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("OrganismValidator not supported for class " + target.getClass());
    }
    Organism organism = (Organism) target;
    checkDetermination(organism, errors);
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
    errors.rejectValue(field, messageKey,
        messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale()));
  }
}
