package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.entities.Expedition;
import ca.gc.aafc.dina.validation.DinaBaseValidator;

@Component
public class ExpeditionValidator extends DinaBaseValidator<Expedition> {

  static final String VALID_EVENT_DATE_KEY = "validation.constraint.violation.validEventDateTime";

  public ExpeditionValidator(MessageSource messageSource) {
    super(Expedition.class, messageSource);
  }

  @Override
  public void validateTarget(Expedition target, Errors errors) {
    validateStartEndDate(target, errors);
  }

  private void validateStartEndDate(Expedition project, Errors errors) {
    if (project.getStartDate() == null && project.getEndDate() != null ||
      project.getEndDate() != null && project.getStartDate()
        .isAfter(project.getEndDate())) {
      addError(errors, VALID_EVENT_DATE_KEY);
    }
  }

  /**
   * Internal method to add an error to the provided Errors object with a message from the
   * message bundle.
   * @param errors
   * @param messageBundleKey
   */
  private void addError(Errors errors, String messageBundleKey) {
    String errorMessage = getMessage(messageBundleKey);
    errors.reject(messageBundleKey, errorMessage);
  }
}
