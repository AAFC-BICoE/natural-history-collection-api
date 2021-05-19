package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.MaterialSample;

@Component
public class MaterialSampleValidator implements Validator {

  private final MessageSource messageSource;

  public MaterialSampleValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  static final String VALID_PARENT_RELATIONSHIP_LOOP = "validation.constraint.violation.loopingParentMaterialSample";

  @Override
  public boolean supports(Class<?> clazz) {
    return MaterialSample.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("MaterialSampleValidator not supported for class " + target.getClass());
    }    
    MaterialSample materialSample = (MaterialSample) target;
    detectLoop(materialSample, errors);
  }

  private void detectLoop(MaterialSample materialSample, Errors errors) {
    MaterialSample slow = materialSample.getParentMaterialSample();
    MaterialSample fast = materialSample.getParentMaterialSample();
    boolean isLooping = false;

    while (slow != null && fast != null && fast.getParentMaterialSample() != null) {
      slow = slow.getParentMaterialSample();
      fast = fast.getParentMaterialSample().getParentMaterialSample();
      if (slow == fast) {
        isLooping = true;
        break;
      }
    }
    if (isLooping) {
      String errorMessage = messageSource.getMessage(VALID_PARENT_RELATIONSHIP_LOOP,
        null, LocaleContextHolder.getLocale());
      errors.rejectValue(
        "parentMaterialSample",
        VALID_PARENT_RELATIONSHIP_LOOP,
        errorMessage);
    }
  }
  
}
