package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import lombok.NonNull;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CollectingEventValidator implements Validator {

  private final MessageSource messageSource;

  public CollectingEventValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return CollectingEvent.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("CollectingEventValidator not supported for class " + target.getClass());
    }
    CollectingEvent collectingEvent = (CollectingEvent) target;
    if ((collectingEvent.getStartEventDateTime() == null && collectingEvent.getEndEventDateTime() != null)
      || (collectingEvent.getEndEventDateTime() != null
      && collectingEvent.getStartEventDateTime().isAfter(collectingEvent.getEndEventDateTime()))) {
      String errorMessage = messageSource.getMessage(
        "validation.constraint.violation.validEventDateTime",
        null, LocaleContextHolder.getLocale());
      errors.reject("validation.constraint.violation.validEventDateTime", errorMessage);
    }
  }
}
