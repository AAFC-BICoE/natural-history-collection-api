package ca.gc.aafc.collection.api.validation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.MaterialSample;

import lombok.NonNull;

@Component
public class MaterialSampleValidator implements Validator {

  private final MessageSource messageSource;

  public MaterialSampleValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public static final String VALID_PARENT_RELATIONSHIP_LOOP = "validation.constraint.violation.loopingParentMaterialSample";
  public static final String PARENT_AND_EVENT_ERROR_KEY = "validation.constraint.violation.sample.parentWithEvent";
  public static final String VALID_DETERMINATION_SCIENTIFICNAMESOURCE = "validation.constraint.violation.determination.scientificnamesource";
  public static final String VALID_DETERMINATION_SCIENTIFICNAME = "validation.constraint.violation.determination.scientificname";
  public static final String MISSING_PRIMARY_DETERMINATION = "validation.constraint.violation.determination.primaryDeterminationMissing";
  public static final String MORE_THAN_ONE_ISFILEDAS = "validation.constraint.violation.determination.moreThanOneIsFiledAs";

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return MaterialSample.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("MaterialSampleValidator not supported for class " + target.getClass());
    }
    MaterialSample materialSample = (MaterialSample) target;
    checkParentIsNotSelf(errors, materialSample);
    checkHasParentOrEventOrAcquisitionEvent(errors, materialSample);
    checkDetermination(errors, materialSample);
  }

  private void checkHasParentOrEventOrAcquisitionEvent(Errors errors, MaterialSample materialSample) {
    if (isMoreThanOne(materialSample.getParentMaterialSample() != null, materialSample.getCollectingEvent() != null, materialSample.getAcquisitionEvent() != null)) {
      String errorMessage = getMessage(PARENT_AND_EVENT_ERROR_KEY);
      errors.rejectValue("parentMaterialSample", PARENT_AND_EVENT_ERROR_KEY, errorMessage);
    }
  }

  private void checkParentIsNotSelf(Errors errors, MaterialSample materialSample) {
    if (materialSample.getParentMaterialSample() != null
      && materialSample.getParentMaterialSample().getUuid().equals(materialSample.getUuid())) {
      String errorMessage = getMessage(VALID_PARENT_RELATIONSHIP_LOOP);
      errors.rejectValue("parentMaterialSample", VALID_PARENT_RELATIONSHIP_LOOP, errorMessage);
    }
  }

  private void checkDetermination(Errors errors, MaterialSample materialSample) {

    // Determinations are stored on each organism on a material sample.
    if (CollectionUtils.isNotEmpty(materialSample.getOrganism())) {
      materialSample.getOrganism().forEach(organism -> {

        // For each organism, these determination rules apply
        if (CollectionUtils.isNotEmpty(organism.getDetermination())) {

          // Ensure the correct number of primary determination is saved.
          if (organism.countPrimaryDetermination() != 1) {
            // Other types must always have 1 primary determination.
            errors.rejectValue(
              "organism",
              MISSING_PRIMARY_DETERMINATION,
              getMessage(MISSING_PRIMARY_DETERMINATION));
          }
    
          // Ensure scientific name and verbatim are set correctly.
          int isFiledAsCounter = 0;
          for (Determination determination : organism.getDetermination()) {
            // XOR, both set or both not set but never only one of them
            if (determination.getScientificNameSource() == null ^ StringUtils.isBlank(determination.getScientificName())) {
              String errorMessage = getMessage(VALID_DETERMINATION_SCIENTIFICNAMESOURCE);
              errors.rejectValue("organism", VALID_DETERMINATION_SCIENTIFICNAMESOURCE, errorMessage);
            }
            if (StringUtils.isBlank(determination.getVerbatimScientificName()) && StringUtils.isBlank(determination.getScientificName())) {
              String errorMessage = getMessage(VALID_DETERMINATION_SCIENTIFICNAME);
              errors.rejectValue("organism", VALID_DETERMINATION_SCIENTIFICNAME, errorMessage);
            }
            // Count if isFiled as is set.
            if (determination.getIsFileAs() != null && determination.getIsFileAs()) {
              isFiledAsCounter++;
            }
          }
    
          // Check there is 0 or 1 isFiledAs but never more
          if (isFiledAsCounter > 1) {
            String errorMessage = getMessage(MORE_THAN_ONE_ISFILEDAS);
            errors.rejectValue("organism", MORE_THAN_ONE_ISFILEDAS, errorMessage);
          }
        }

      });
    }
  }
  
  private Boolean isMoreThanOne(boolean b1, boolean b2, boolean b3) {
    return b1 && b2 || b1 && b3 || b2 && b3;
  }


  private String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }

}
