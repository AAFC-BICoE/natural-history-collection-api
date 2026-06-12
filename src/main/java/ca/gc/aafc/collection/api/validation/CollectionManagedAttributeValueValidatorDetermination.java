package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.dina.service.ControlledVocabularyItemService;
import ca.gc.aafc.dina.validation.ManagedAttributeValueValidatorV2;

import static ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration.MANAGED_ATTRIBUTE_VOCAB_UUID;

import jakarta.inject.Named;
import java.util.UUID;
import lombok.NonNull;

/**
 * For DETERMINATION managed attribute
 */
@Component
public class CollectionManagedAttributeValueValidatorDetermination extends ManagedAttributeValueValidatorV2<CollectionControlledVocabularyItem> {

  public CollectionManagedAttributeValueValidatorDetermination(@Named("validationMessageSource")MessageSource messageSource,
                                                               @NonNull ControlledVocabularyItemService<CollectionControlledVocabularyItem> vocabItemService) {
    super(messageSource, vocabItemService);
  }

  @Override
  public UUID getControlledVocabularyUuid() {
    return MANAGED_ATTRIBUTE_VOCAB_UUID;
  }

  @Override
  public String getDinaComponent() {
    return CollectionVocabularyConfiguration.DinaComponent.DETERMINATION.name();
  }

  @Override
  public boolean canBeDeleted(CollectionControlledVocabularyItem controlledVocabularyItem) {
    //will be fixed in another ticket, it is just to mimic the previous behavior
    return true;
  }
}
