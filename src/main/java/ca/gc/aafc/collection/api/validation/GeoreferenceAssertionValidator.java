package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;

@Component
public class GeoreferenceAssertionValidator implements Validator {

    private MessageSource messageSource;

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

        if (georeferenceAssertion.getCollectingEvent() != null && georeferenceAssertion.getCollectingEvent().getPrimaryGeoreferenceAssertion() == null) {
            String errorMessage = messageSource.getMessage("georeferenceAssertion.collectingEvent.primaryGeoreferenceAssertion.null",
                null, LocaleContextHolder.getLocale());
            errors.rejectValue("collectingEvent", "georeferenceAssertion.collectingEvent.primaryGeoreferenceAssertion.null", errorMessage);
        }
        
    }
    
}
