package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.testsupport.factories.GeoreferenceAssertionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

class CollectingEventValidatorTest extends CollectionModuleBaseIT {

  @Inject
  private CollectingEventValidator validator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_WhenNoAssertions_ValidationSuccess() {
    CollectingEvent event = newEvent();

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(0, errors.getAllErrors().size());
  }

  @Test
  void validate_WhenOneAssertionIsPrimary_ValidationSuccess() {
    GeoreferenceAssertion assertion = newAssertion();
    assertion.setIsPrimary(true);
    GeoreferenceAssertion assertion2 = newAssertion();
    assertion2.setIsPrimary(false);

    CollectingEvent event = newEvent();
    event.setGeoReferenceAssertions(List.of(assertion, assertion2));

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(0, errors.getAllErrors().size());
  }

  @Test
  void validate_MoreThenOnePrimaryAssertion_ErrorsReturned() {
    String expectedErrorMessage = messageSource.getMessage(
      CollectingEventValidator.VALID_PRIMARY_KEY,
      null,
      LocaleContextHolder.getLocale());

    GeoreferenceAssertion assertion = newAssertion();
    assertion.setIsPrimary(true);

    CollectingEvent event = newEvent();
    event.setGeoReferenceAssertions(List.of(assertion, assertion));

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  @Test
  void validate_NoAssertionsArePrimary_ErrorsReturned() {
    String expectedErrorMessage = messageSource.getMessage(
      CollectingEventValidator.VALID_PRIMARY_KEY,
      null,
      LocaleContextHolder.getLocale());

    GeoreferenceAssertion assertion = newAssertion();
    assertion.setIsPrimary(false);

    CollectingEvent event = newEvent();
    event.setGeoReferenceAssertions(List.of(assertion, assertion));

    Errors errors = new BeanPropertyBindingResult(event, event.getUuid().toString());
    validator.validate(event, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  private static CollectingEvent newEvent() {
    return CollectingEvent.builder()
      .uuid(UUID.randomUUID())
      .startEventDateTime(LocalDateTime.now().minusDays(1))
      .build();
  }

  private static GeoreferenceAssertion newAssertion() {
    return GeoreferenceAssertionFactory.newGeoreferenceAssertion()
      .dwcDecimalLatitude(12.123456)
      .dwcDecimalLongitude(45.01)
      .dwcCoordinateUncertaintyInMeters(10)
      .isPrimary(false)
      .build();
  }
}