package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

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
    checkHasParentOrEvent(errors, materialSample);
    checkDetermination(errors, materialSample);
  }

  private void checkHasParentOrEvent(Errors errors, MaterialSample materialSample) {
    if (materialSample.getParentMaterialSample() != null && materialSample.getCollectingEvent() != null) {
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
    if (CollectionUtils.isNotEmpty(materialSample.getDetermination())) {
      if (countPrimaries(materialSample.getDetermination()) != 1) {
        errors.rejectValue(
          "determination",
          MISSING_PRIMARY_DETERMINATION,
          getMessage(MISSING_PRIMARY_DETERMINATION));
      }
      for (Determination determination : materialSample.getDetermination()) {
        // XOR, both set or both not set but never only one of them
        if (determination.getScientificNameSource() == null ^ StringUtils.isBlank(determination.getScientificName())) {
          String errorMessage = getMessage(VALID_DETERMINATION_SCIENTIFICNAMESOURCE);
          errors.rejectValue("determination", VALID_DETERMINATION_SCIENTIFICNAMESOURCE, errorMessage);
        }
        if (StringUtils.isBlank(determination.getVerbatimScientificName()) && StringUtils.isBlank(determination.getScientificName())) {
          String errorMessage = getMessage(VALID_DETERMINATION_SCIENTIFICNAME);
          errors.rejectValue("determination", VALID_DETERMINATION_SCIENTIFICNAME, errorMessage);
        }
      }
    }
  }

  private static long countPrimaries(List<Determination> determinations) {
    if (CollectionUtils.isEmpty(determinations)) {
      return 0;
    }
    return determinations.stream().filter(d -> d.getIsPrimary() != null && d.getIsPrimary()).count();
  }

  private String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }

}
