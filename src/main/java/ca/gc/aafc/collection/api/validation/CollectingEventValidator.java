package ca.gc.aafc.collection.api.validation;

import java.util.List;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.CollectingEvent;

@Component
public class CollectingEventValidator implements Validator {
    
    private MessageSource messageSource;

    public CollectingEventValidator(MessageSource messageSource) {
      this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
      return CollectingEvent.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
      CollectingEvent collectingEvent = (CollectingEvent) target;
      int assertionsSize = Optional.ofNullable(collectingEvent.getOtherGeoReferenceAssertions())
      .map(List::size).orElse(0);
      if ((collectingEvent.getStartEventDateTime() == null && collectingEvent.getEndEventDateTime() != null)
            || (collectingEvent.getEndEventDateTime() != null
              && collectingEvent.getStartEventDateTime().isAfter(collectingEvent.getEndEventDateTime()))) {
          String errorMessage = messageSource.getMessage("validation.constraint.violation.validEventDateTime", null,
            LocaleContextHolder.getLocale());
          errors.reject("validation.constraint.violation.validEventDateTime", errorMessage);
        }
      if (assertionsSize > 0 && collectingEvent.getPrimaryGeoreferenceAssertion() == null) {
          String errorMessage = messageSource.getMessage("collectingEvent.primaryGeoreferenceAssertion.null",
          null, LocaleContextHolder.getLocale());
          errors.rejectValue("primaryGeoreferenceAssertion", "collectingEvent.primaryGeoreferenceAssertion.null", errorMessage);
      } 
      if (assertionsSize > 0 && collectingEvent.getOtherGeoReferenceAssertions().contains(collectingEvent.getPrimaryGeoreferenceAssertion())) {
          String errorMessage = messageSource.getMessage("collectingEvent.primaryGeoreferenceAssertion.inList",
          null, LocaleContextHolder.getLocale());
          errors.rejectValue("primaryGeoreferenceAssertion", "collectingEvent.primaryGeoreferenceAssertion.inList", errorMessage);
      }
    }

    
}
