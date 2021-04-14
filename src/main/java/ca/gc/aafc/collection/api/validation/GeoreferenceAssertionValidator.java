package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion.GeoreferenceVerificationStatus;
import lombok.NonNull;

@Component
public class GeoreferenceAssertionValidator implements Validator {

  private final MessageSource messageSource;

  public GeoreferenceAssertionValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return GeoreferenceAssertion.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      String errorMessage = messageSource.getMessage("classCast.invalid", new String[] {"GeoreferenceAssertion"},
        LocaleContextHolder.getLocale());
      errors.reject("georeferenceAssertion.GeoreferenceVerificationStatus.invalid", errorMessage);   
    }
    GeoreferenceAssertion georeferenceAssertion = (GeoreferenceAssertion) target;
    
    if ((georeferenceAssertion.getDwcGeoreferenceVerificationStatus() == GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE) &&
        (georeferenceAssertion.getDwcDecimalLatitude() != null || georeferenceAssertion.getDwcDecimalLongitude() != null || georeferenceAssertion.getDwcCoordinateUncertaintyInMeters() != null)) {
            String errorMessage = messageSource.getMessage("georeferenceAssertion.GeoreferenceVerificationStatus.invalid", null,
                LocaleContextHolder.getLocale());
            errors.rejectValue("dwcGeoreferenceVerificationStatus","georeferenceAssertion.GeoreferenceVerificationStatus.invalid", errorMessage);   
        }

    if (georeferenceAssertion.getCollectingEvent() != null && georeferenceAssertion.getCollectingEvent().getPrimaryGeoreferenceAssertion() == null) {
        String errorMessage = messageSource.getMessage("georeferenceAssertion.collectingEvent.primaryGeoreferenceAssertion.null",
            null, LocaleContextHolder.getLocale());
        errors.rejectValue("collectingEvent", "georeferenceAssertion.collectingEvent.primaryGeoreferenceAssertion.null", errorMessage);
    }
  }
}
