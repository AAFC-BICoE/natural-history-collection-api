package ca.gc.aafc.collection.api.validation;

import javax.inject.Named;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.dina.validation.ControlledVocabularyItemValidator;

@Component
public class CollectionControlledVocabularyItemValidator extends ControlledVocabularyItemValidator {
  public CollectionControlledVocabularyItemValidator(@Named("validationMessageSource") MessageSource messageSource) {
    super(messageSource);
  }
}
