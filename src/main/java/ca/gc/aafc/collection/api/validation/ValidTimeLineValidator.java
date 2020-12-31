package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.entities.CollectingEvent;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidTimeLineValidator implements ConstraintValidator<ValidTimeLine, CollectingEvent> {
  @Override
  public boolean isValid(CollectingEvent event, ConstraintValidatorContext context) {
    if (event.getStartEventDateTime() == null) {
      return event.getEndEventDateTime() == null;
    }
    if (event.getEndEventDateTime() != null) {
      return event.getStartEventDateTime().isBefore(event.getEndEventDateTime());
    }
    return true;
  }
}
