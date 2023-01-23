package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.entities.DinaComponent;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class MaterialSampleExtensionValueValidator extends BaseExtensionValueValidator {

  public MaterialSampleExtensionValueValidator(
          MessageSource messageSource,
          CollectionExtensionConfiguration collectionExtensionConfiguration
  ) {
    super(DinaComponent.MATERIAL_SAMPLE, messageSource, collectionExtensionConfiguration);
  }
}