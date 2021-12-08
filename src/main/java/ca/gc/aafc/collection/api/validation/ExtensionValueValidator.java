package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Extension;
import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Field;

import ca.gc.aafc.collection.api.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.entities.ExtensionValue;
import lombok.NonNull;

@Component
public class ExtensionValueValidator implements Validator {

  private final MessageSource messageSource;

  private final CollectionExtensionConfiguration collectionExtensionConfiguration;

  public static final String NO_MATCH_KEY_VERSION = "validation.constraint.violation.noMatchKeyVersion";
  public static final String NO_MATCH_TERM = "validation.constraint.violation.noMatchTerm";

  public ExtensionValueValidator(
    MessageSource messageSource, 
    CollectionExtensionConfiguration collectionExtensionConfiguration
  ) {
    this.messageSource = messageSource;
    this.collectionExtensionConfiguration = collectionExtensionConfiguration;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return ExtensionValue.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("ExtensionValueValidator not supported for class " + target.getClass());
    }
    ExtensionValue extensionValue = (ExtensionValue) target;
    matchCollectionExtensionConfigurationKeyAndVersion(errors, extensionValue);
    
  }

  private void matchCollectionExtensionConfigurationKeyAndVersion(Errors errors, ExtensionValue extensionValue) {
    for (Extension extension : collectionExtensionConfiguration.getExtension().values()) {
      if (extension.getKey().equals(extensionValue.getExtKey()) && 
        extension.getVersion().equals(extensionValue.getExtVersion())) {
          matchCollectionExtensionConfigurationTerm(errors, extensionValue, extension);
          return;
      }
    }
    String errorMessage = getMessageForKey(
      NO_MATCH_KEY_VERSION, 
      extensionValue.getExtKey(),
      extensionValue.getExtVersion());
    errors.rejectValue("extKey", errorMessage);
  }

  private void matchCollectionExtensionConfigurationTerm(Errors errors, ExtensionValue extensionValue, Extension extension) {
    for (Field field : extension.getFields()) {
      if (field.getTerm().equals(extensionValue.getExtTerm())) {
        return;
      }
    }
    String errorMessage = getMessageForKey(
      NO_MATCH_TERM, 
      extensionValue.getExtKey(),
      extensionValue.getExtVersion(),
      extensionValue.getExtTerm());
    errors.rejectValue("extTerm", errorMessage);
  }

  private String getMessageForKey(String key, Object... objects) {
    return messageSource.getMessage(key, objects, LocaleContextHolder.getLocale());
  }
}
