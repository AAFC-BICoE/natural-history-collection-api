package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.entities.CollectingEvent;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Used for bean validation of a {@link ca.gc.aafc.collection.api.entities.CollectingEvent}'s timeline.
 * An event date time is valid if an end date is never present without a start date, and a start date
 * always proceeds an end date.
 */
public class ValidEventDateTimeValidator implements ConstraintValidator<ValidEventDateTime, CollectingEvent> {
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
