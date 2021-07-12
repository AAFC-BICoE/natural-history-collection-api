package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class MaterialSampleValidator implements Validator {

  private final MessageSource messageSource;
  private final SmartValidator validator;

  public MaterialSampleValidator(MessageSource messageSource, SmartValidator validator) {
    this.messageSource = messageSource;
    this.validator = validator;
  }

  public static final String VALID_PARENT_RELATIONSHIP_LOOP = "validation.constraint.violation.loopingParentMaterialSample";
  public static final String PARENT_AND_EVENT_ERROR_KEY = "validation.constraint.violation.sample.parentWithEvent";
  public static final String DETERMINATION_ERROR_KEY = "validation.constraint.violation.sample.determination.constraintError";
  public static final String DETERMINATION_DETAIL_ERROR_KEY = "validation.constraint.violation.sample.determination.detail.constraintError";

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

  private void checkDetermination(Errors errors, MaterialSample materialSample) {
    Determination determination = materialSample.getDetermination();
    if (determination != null) {
      Errors determinationErrors = ValidationErrorsHelper.newErrorsObject(materialSample);
      validator.validate(determination, determinationErrors);

      determinationErrors.getFieldErrors()
        .forEach(fieldError -> errors.rejectValue("determination", DETERMINATION_ERROR_KEY,
          getMessage(DETERMINATION_ERROR_KEY, fieldErrorToObjects(fieldError))));

      List<Determination.DeterminationDetail> details = determination.getDetails();
      if (CollectionUtils.isNotEmpty(details)) {
        details.forEach(determinationDetail -> {
          Errors detailErrors = ValidationErrorsHelper.newErrorsObject(materialSample);
          validator.validate(determinationDetail, detailErrors);
          detailErrors.getFieldErrors()
            .forEach(fieldError -> errors.rejectValue("determination", DETERMINATION_DETAIL_ERROR_KEY,
              getMessage(DETERMINATION_DETAIL_ERROR_KEY, fieldErrorToObjects(fieldError))));
        });

      }
    }
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

  private String getMessage(String key, Object[] objects) {
    return messageSource.getMessage(key, objects, LocaleContextHolder.getLocale());
  }

  private String getMessage(String key) {
    return this.getMessage(key, null);
  }

  private static Object[] fieldErrorToObjects(FieldError fieldError) {
    return new Object[]{
      fieldError.getField(), fieldError.getDefaultMessage()
    };
  }
}
