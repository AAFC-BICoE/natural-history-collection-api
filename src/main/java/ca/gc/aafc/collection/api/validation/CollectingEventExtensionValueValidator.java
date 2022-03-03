package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.collection.api.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.entities.DinaComponent;

@Component
public class CollectingEventExtensionValueValidator extends BaseExtensionValueValidator {

  public CollectingEventExtensionValueValidator(
    MessageSource messageSource, 
    CollectionExtensionConfiguration collectionExtensionConfiguration
  ) {
    super(DinaComponent.COLLECTING_EVENT, messageSource, collectionExtensionConfiguration);
  }
}
