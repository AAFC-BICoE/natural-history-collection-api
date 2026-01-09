package ca.gc.aafc.collection.api.validation;

import lombok.NonNull;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.collection.api.entities.CollectionControlledVocabulary;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.dina.service.ControlledVocabularyItemService;
import ca.gc.aafc.dina.service.ControlledVocabularyService;
import ca.gc.aafc.dina.validation.ControlledTermValueValidator;

@Component
public class CollectionControlledTermValueValidator extends ControlledTermValueValidator<CollectionControlledVocabulary, CollectionControlledVocabularyItem> {

  public CollectionControlledTermValueValidator(
    MessageSource messageSource,
    ControlledVocabularyService<CollectionControlledVocabulary> vocabService,
    @NonNull ControlledVocabularyItemService<CollectionControlledVocabularyItem> vocabItemService) {
    super(messageSource, vocabService, vocabItemService);
  }
}
