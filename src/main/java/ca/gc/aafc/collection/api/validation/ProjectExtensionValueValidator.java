package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.collection.api.config.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.entities.DinaComponent;

@Component
public class ProjectExtensionValueValidator extends BaseExtensionValueValidator {

  public ProjectExtensionValueValidator(
    MessageSource messageSource,
    CollectionExtensionConfiguration collectionExtensionConfiguration
  ) {
    super(DinaComponent.PROJECT, messageSource, collectionExtensionConfiguration);
  }
}
