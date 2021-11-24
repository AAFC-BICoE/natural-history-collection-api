package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.Project;
import lombok.NonNull;

@Component
public class ProjectValidator implements Validator {

  private final MessageSource messageSource;

  static final String VALID_EVENT_DATE_KEY = "validation.constraint.violation.validEventDateTime";

  public ProjectValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return Project.class.isAssignableFrom(clazz);

  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("ProjectValidator not supported for class " + target.getClass());
    }
    Project project = (Project) target;
    validateStartEndDate(errors, project);
  }

  private void validateStartEndDate(Errors errors, Project project) {
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
    String errorMessage = messageSource.getMessage(
        messageBundleKey,
        null,
        LocaleContextHolder.getLocale());
    errors.reject(messageBundleKey, errorMessage);
  }
}
