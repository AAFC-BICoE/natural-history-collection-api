package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion.GeoreferenceVerificationStatus;

@Component
public class GeoreferenceAssertionValidator implements Validator {

    private final MessageSource messageSource;

    public GeoreferenceAssertionValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return GeoreferenceAssertion.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        GeoreferenceAssertion georeferenceAssertion = (GeoreferenceAssertion) target;
        
        if ((georeferenceAssertion.getDwcGeoreferenceVerificationStatus() == GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE) &&
            (georeferenceAssertion.getDwcDecimalLatitude() != null || georeferenceAssertion.getDwcDecimalLongitude() != null || georeferenceAssertion.getDwcCoordinateUncertaintyInMeters() != null)) {
                String errorMessage = messageSource.getMessage("georeferenceAssertion.GeoreferenceVerificationStatus.invalid", null,
                    LocaleContextHolder.getLocale());
                errors.rejectValue("dwcGeoreferenceVerificationStatus","georeferenceAssertion.GeoreferenceVerificationStatus.invalid", errorMessage);   
            }
    }
    
}
