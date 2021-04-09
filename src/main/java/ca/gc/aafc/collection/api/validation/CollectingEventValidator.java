package src.main.java.ca.gc.aafc.collection.api.validation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.CollectingEvent;

@Component
public class CollectingEventValidator implements Validator {
    
    @Inject
    private MessageSource messageSource;

    public void CollectingEventPrimaryGeoreferenceValidator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return CollectingEvent.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CollectingEvent collectingEvent = (CollectingEvent) collectingEvent;
        int assertionsSize = Optional.ofNullable(collectingEvent.getGeoReferenceAssertions())
        .map(List::size).orElse(0);

        if (assertionsSize > 0 && collectingEvent.getPrimaryGeoreferenceAssertion() != null) {
            String errorMessage = messageSource.getMessage("primaryGeoreference.null",
            null, LocaleContextHolder.getLocale());
            errors.rejectValue("primaryGeoreference", "primaryGeoreference.null", errorMessage);
        } 
        if (collectingEvent.getGeoReferenceAssertions().contains(collectingEvent.getPrimaryGeoreferenceAssertion())) {
            String errorMessage = messageSource.getMessage("primaryGeoreference.inList",
            null, LocaleContextHolder.getLocale());
            errors.rejectValue("primaryGeoreference", "primaryGeoreference.inList", errorMessage);
        }
    }

    
}
