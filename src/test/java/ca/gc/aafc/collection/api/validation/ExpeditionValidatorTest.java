package ca.gc.aafc.collection.api.validation;

import java.time.LocalDate;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Expedition;
import ca.gc.aafc.collection.api.testsupport.factories.ExpeditionFactory;

public class ExpeditionValidatorTest extends CollectionModuleBaseIT {

  @Inject
  private ExpeditionValidator validator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_NoValidationException_ValidationSuccess() {
    Expedition expedition = ExpeditionFactory.newExpedition().build();

    Errors errors = new BeanPropertyBindingResult(expedition, expedition.getUuid().toString());
    validator.validate(expedition, errors);
    Assertions.assertEquals(0, errors.getAllErrors().size());
  }

  @Test
  void validate_EndDateBeforeStartDate_ErrorsReturned() {
    String expectedErrorMessage = getExpectedErrorMessage(ExpeditionValidator.VALID_EVENT_DATE_KEY);

    Expedition expedition = ExpeditionFactory.newExpedition()
      .startDate(LocalDate.of(2021, 01, 01))
      .endDate(LocalDate.of(2001, 01, 01))
      .build();

    Errors errors = new BeanPropertyBindingResult(expedition, expedition.getUuid().toString());
    validator.validate(expedition, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertEquals(expectedErrorMessage, errors.getAllErrors().get(0).getDefaultMessage());
  }

  private String getExpectedErrorMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }

}
