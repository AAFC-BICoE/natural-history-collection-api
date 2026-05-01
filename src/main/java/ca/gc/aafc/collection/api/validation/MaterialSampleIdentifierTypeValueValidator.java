package ca.gc.aafc.collection.api.validation;

import jakarta.inject.Named;
import lombok.NonNull;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.collection.api.entities.DinaComponent;
import ca.gc.aafc.dina.service.ControlledVocabularyItemService;

/**
 * Specific for material-sample usage
 */
@Component
public class MaterialSampleIdentifierTypeValueValidator extends IdentifierTypeValueValidator {

  public MaterialSampleIdentifierTypeValueValidator(
    @Named("validationMessageSource") MessageSource messageSource,
    @NonNull ControlledVocabularyItemService<CollectionControlledVocabularyItem> vocabItemService) {
    super(messageSource, vocabItemService);
  }

  @Override
  public String getDinaComponent() {
    return DinaComponent.MATERIAL_SAMPLE.name();
  }
}
