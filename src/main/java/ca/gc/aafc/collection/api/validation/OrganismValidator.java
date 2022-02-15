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

  public static final String VALID_DETERMINATION_SCIENTIFICNAMESOURCE = "validation.constraint.violation.determination.scientificnamesource";
  public static final String VALID_DETERMINATION_SCIENTIFICNAME = "validation.constraint.violation.determination.scientificname";
  public static final String MISSING_PRIMARY_DETERMINATION = "validation.constraint.violation.determination.primaryDeterminationMissing";
  public static final String MORE_THAN_ONE_ISFILEDAS = "validation.constraint.violation.determination.moreThanOneIsFiledAs";

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

      // Ensure the correct number of primary determination is saved.
      if (organism.countPrimaryDetermination() != 1) {
        // Other types must always have 1 primary determination.
        errors.rejectValue("determination", MISSING_PRIMARY_DETERMINATION,
            getMessage(MISSING_PRIMARY_DETERMINATION));
      }

      // Ensure scientific name and verbatim are set correctly.
      int isFiledAsCounter = 0;
      for (Determination determination : organism.getDetermination()) {
        // XOR, both set or both not set but never only one of them
        if (determination.getScientificNameSource() == null ^ StringUtils
            .isBlank(determination.getScientificName())) {
          String errorMessage = getMessage(VALID_DETERMINATION_SCIENTIFICNAMESOURCE);
          errors.rejectValue("determination", VALID_DETERMINATION_SCIENTIFICNAMESOURCE, errorMessage);
        }
        if (StringUtils.isBlank(determination.getVerbatimScientificName()) && StringUtils
            .isBlank(determination.getScientificName())) {
          String errorMessage = getMessage(VALID_DETERMINATION_SCIENTIFICNAME);
          errors.rejectValue("determination", VALID_DETERMINATION_SCIENTIFICNAME, errorMessage);
        }
        // Count if isFiled as is set.
        if (determination.getIsFileAs() != null && determination.getIsFileAs()) {
          isFiledAsCounter++;
        }
      }

      // Check there is 0 or 1 isFiledAs but never more
      if (isFiledAsCounter > 1) {
        String errorMessage = getMessage(MORE_THAN_ONE_ISFILEDAS);
        errors.rejectValue("determination", MORE_THAN_ONE_ISFILEDAS, errorMessage);
      }
    }
  }

  private String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
}
