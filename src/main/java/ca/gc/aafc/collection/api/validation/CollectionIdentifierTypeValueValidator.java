package ca.gc.aafc.collection.api.validation;

import jakarta.inject.Named;
import lombok.NonNull;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.dina.service.ControlledVocabularyItemService;

/**
 * Specific for collection usage
 */
@Component
public class CollectionIdentifierTypeValueValidator extends IdentifierTypeValueValidator {

  public CollectionIdentifierTypeValueValidator (
    @Named("validationMessageSource") MessageSource messageSource,
    @NonNull ControlledVocabularyItemService<CollectionControlledVocabularyItem> vocabItemService) {
    super(messageSource, vocabItemService);
  }

  @Override
  public String getDinaComponent() {
    return CollectionVocabularyConfiguration.DinaComponent.COLLECTION.name();
  }
}
