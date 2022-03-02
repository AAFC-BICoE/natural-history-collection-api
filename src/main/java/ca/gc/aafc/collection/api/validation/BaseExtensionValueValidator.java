package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.entities.DinaComponent;
import ca.gc.aafc.collection.api.entities.ExtensionValue;
import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Extension;
import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Field;
import lombok.NonNull;

public class BaseExtensionValueValidator implements Validator {

  public static final String NO_MATCH_KEY_VERSION = "validation.constraint.violation.noMatchKeyVersion";
  public static final String NO_MATCH_TERM = "validation.constraint.violation.noMatchTerm";
  public static final String NO_MATCH_ACCEPTED_VALUE = "validation.constraint.violation.noMatchAcceptedValue";
  public static final String INCORRECT_DINA_COMPONENT = "validation.constraint.violation.incorrectDinaComponent";

  private final DinaComponent componentType;
  private final MessageSource messageSource;
  private final CollectionExtensionConfiguration configuration;

  public BaseExtensionValueValidator(
      DinaComponent componentType, 
      MessageSource messageSource, 
      CollectionExtensionConfiguration configuration) {
    this.componentType = componentType;
    this.messageSource = messageSource;
    this.configuration = configuration;
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

  /**
   * Check if the key and version match. Once a match is found, the term is searched to ensure
   * it exists within the configuration.
   * 
   * @param errors if any validation problems occur, errors will be reported here.
   * @param extensionValue extension value being validated against.
   */
  private void matchCollectionExtensionConfigurationKeyAndVersion(Errors errors, ExtensionValue extensionValue) {
    for (Extension extension : configuration.getExtension().values()) {
      if (extension.matchesKeyVersion(extensionValue.getExtKey(), extensionValue.getExtVersion())) {
        matchCollectionExtensionConfigurationTerm(errors, extensionValue, extension);
        return;
      }
    }

    String errorMessage = getMessageForKey(
      NO_MATCH_KEY_VERSION, 
      extensionValue.getExtKey(),
      extensionValue.getExtVersion());
    errors.rejectValue("extKey", NO_MATCH_KEY_VERSION, errorMessage);
  }

  /**
   * Checks to see if the term exists within the extension set. If a match is found, the
   * next step is to look at the dinaComponent to ensure it's allowed to be used where the
   * extension value is being saved.
   * 
   * @param errors if any validation problems occur, errors will be reported here.
   * @param extensionValue extension value being validated against.
   * @param extension the extension set of values that that matches the key and version.
   */
  private void matchCollectionExtensionConfigurationTerm(Errors errors, ExtensionValue extensionValue, Extension extension) {

    if(extension.containsTerm(extensionValue.getExtTerm())) {
      // Extension term was found, but ensure it's the same component.
      matchCollectionExtensionConfigurationComponent(errors, extensionValue, extension.getFieldByTerm(extensionValue.getExtTerm()));
      return;
    }

    String errorMessage = getMessageForKey(
      NO_MATCH_TERM, 
      extensionValue.getExtKey(),
      extensionValue.getExtVersion(),
      extensionValue.getExtTerm());
    errors.rejectValue("extTerm", NO_MATCH_TERM, errorMessage);
  }

  /**
   * Looks at the dinaComponent to ensure it's allowed to be used where the
   * extension value is being saved. When the validator is set up, a dinaComponent is setup
   * 
   * @param errors if any validation problems occur, errors will be reported here.
   * @param extensionValue extension value being validated against.
   * @param field the field that was matched with the term.
   */
  private void matchCollectionExtensionConfigurationComponent(Errors errors, ExtensionValue extensionValue, Field field) {
    DinaComponent extensionComponent = DinaComponent.valueOf(field.getDinaComponent());

    if (extensionComponent.equals(componentType)) {
      // Finally, check if the field has accepted values that the extensionValue needs to follow.
      matchCollectionExtensionConfigurationAcceptedValues(errors, extensionValue, field);

      return;
    }

    String errorMessage = getMessageForKey(
      NO_MATCH_TERM, 
      componentType,
      field.getDinaComponent());
    errors.rejectValue("extTerm", INCORRECT_DINA_COMPONENT, errorMessage);
  }

  /**
   * Checks if the field contains any accepted values. If it does, the extension value provided must
   * match one of those accepted values.
   * 
   * @param errors if any validation problems occur, errors will be reported here.
   * @param extensionValue extension value being validated against.
   * @param field the field that was matched with the term.
   */
  private void matchCollectionExtensionConfigurationAcceptedValues(Errors errors, ExtensionValue extensionValue, Field field) {
    // Check if the field has accepted values, if it does, check that the value provided matches a accepted value.
    if (field.getAcceptedValues() == null || field.isAcceptedValues(extensionValue.getValue())) {
      return;
    }

    String errorMessage = getMessageForKey(
      NO_MATCH_ACCEPTED_VALUE, 
      extensionValue.getValue(),
      field.getAcceptedValues());
    errors.rejectValue("extTerm", NO_MATCH_ACCEPTED_VALUE, errorMessage);
  }

  private String getMessageForKey(String key, Object... objects) {
    return messageSource.getMessage(key, objects, LocaleContextHolder.getLocale());
  }
  
}
