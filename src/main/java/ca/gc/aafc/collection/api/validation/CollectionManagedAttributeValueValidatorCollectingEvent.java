package ca.gc.aafc.collection.api.validation;

import java.util.UUID;
import jakarta.inject.Named;
import lombok.NonNull;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.dina.service.ControlledVocabularyItemService;
import ca.gc.aafc.dina.service.PostgresJsonbService;
import ca.gc.aafc.dina.validation.ManagedAttributeValueValidatorV2;

import static ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration.MANAGED_ATTRIBUTE_VOCAB_UUID;

/**
 * For COLLECTING_EVENT managed attribute
 */
@Component
public class CollectionManagedAttributeValueValidatorCollectingEvent extends ManagedAttributeValueValidatorV2<CollectionControlledVocabularyItem> {

  public static final String COLLECTING_EVENT_TABLE_NAME = "collecting_event";
  public static final String MANAGED_ATTRIBUTES_COL_NAME = "managed_attributes";

  private final PostgresJsonbService jsonbService;

  public CollectionManagedAttributeValueValidatorCollectingEvent(@Named("validationMessageSource")MessageSource messageSource,
                                                                 @NonNull ControlledVocabularyItemService<CollectionControlledVocabularyItem> vocabItemService,
                                                                 PostgresJsonbService jsonbService) {
    super(messageSource, vocabItemService);
    this.jsonbService = jsonbService;
  }

  @Override
  public UUID getControlledVocabularyUuid() {
    return MANAGED_ATTRIBUTE_VOCAB_UUID;
  }

  @Override
  public String getDinaComponent() {
    return CollectionVocabularyConfiguration.DinaComponent.COLLECTING_EVENT.name();
  }

  @Override
  public boolean canBeDeleted(CollectionControlledVocabularyItem controlledVocabularyItem) {
    return jsonbService.countFirstLevelKeys(
      COLLECTING_EVENT_TABLE_NAME, MANAGED_ATTRIBUTES_COL_NAME, controlledVocabularyItem.getKey()) ==
      0;
  }
}
