package ca.gc.aafc.collection.api.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;

public class CollectingEventValidatorTest {

    @Inject
    private MessageSource messageSource;
    
    private CollectingEvent testCollectingEvent;

    private CollectingEventValidator collectingEventValidator = new CollectingEventValidator(messageSource);

    @Test
    public void validStartEndTime_validationPasses() throws Exception {
        testCollectingEvent = CollectingEventFactory.newCollectingEvent()
            .startEventDateTime(LocalDateTime.of(2007, 1, 1, 1, 1, 1))
            .endEventDateTime(LocalDateTime.of(2008, 1, 1, 1, 1, 1))
            .build();
        Errors errors = new BeanPropertyBindingResult(testCollectingEvent, "ce");
        ValidationUtils.invokeValidator(collectingEventValidator, testCollectingEvent, errors);
        assertFalse(errors.hasErrors());
    }

    @Test
    public void nullStartTime_validationPasses() throws Exception {
        testCollectingEvent = CollectingEventFactory.newCollectingEvent()
            .endEventDateTime(LocalDateTime.of(2008, 1, 1, 1, 1, 1))
            .build();
        Errors errors = new BeanPropertyBindingResult(testCollectingEvent, "ce");
        ValidationUtils.invokeValidator(collectingEventValidator, testCollectingEvent, errors);
        assertTrue(errors.hasErrors());
        ObjectError objectError = errors.getGlobalError();
        assertTrue(objectError.getCode().equals("validation.constraint.violation.validEventDateTime"));
        assertTrue(objectError.getDefaultMessage().contains("The start and end dates do not create a valid timeline"));
    }

    @Test
    public void startTimeafterEndTime_validationPasses() throws Exception {
        testCollectingEvent = CollectingEventFactory.newCollectingEvent()
            .startEventDateTime(LocalDateTime.of(2009, 1, 1, 1, 1, 1))
            .endEventDateTime(LocalDateTime.of(2008, 1, 1, 1, 1, 1))
            .build();
        Errors errors = new BeanPropertyBindingResult(testCollectingEvent, "ce");
        ValidationUtils.invokeValidator(collectingEventValidator, testCollectingEvent, errors);
        assertTrue(errors.hasErrors());
        ObjectError objectError = errors.getGlobalError();
        assertTrue(objectError.getCode().equals("validation.constraint.violation.validEventDateTime"));
        assertTrue(objectError.getDefaultMessage().contains("The start and end dates do not create a valid timeline"));
    }
    
}
