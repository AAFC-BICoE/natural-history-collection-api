package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.validation.DinaBaseValidator;

@Component
public class MaterialSampleValidator extends DinaBaseValidator<MaterialSample> {

  public static final String VALID_PARENT_RELATIONSHIP_LOOP = "validation.constraint.violation.loopingParentMaterialSample";
  public static final String PARENT_AND_EVENT_ERROR_KEY = "validation.constraint.violation.sample.parentWithEvent";

  public MaterialSampleValidator(MessageSource messageSource) {
    super(MaterialSample.class, messageSource);
  }

  @Override
  public void validateTarget(MaterialSample target, Errors errors) {
    checkParentIsNotSelf(target, errors);
    checkHasParentOrEventOrAcquisitionEvent(target, errors);
  }

  private void checkHasParentOrEventOrAcquisitionEvent(MaterialSample materialSample, Errors errors) {
    if (materialSample.getParentMaterialSample() != null && materialSample.getCollectingEvent() != null) {
      String errorMessage = getMessage(PARENT_AND_EVENT_ERROR_KEY);
      errors.rejectValue("parentMaterialSample", PARENT_AND_EVENT_ERROR_KEY, errorMessage);
    }
  }

  private void checkParentIsNotSelf(MaterialSample materialSample, Errors errors) {
    if (materialSample.getParentMaterialSample() != null
      && materialSample.getParentMaterialSample().getUuid().equals(materialSample.getUuid())) {
      String errorMessage = getMessage(VALID_PARENT_RELATIONSHIP_LOOP);
      errors.rejectValue("parentMaterialSample", VALID_PARENT_RELATIONSHIP_LOOP, errorMessage);
    }
  }
}
