package ca.gc.aafc.collection.api.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.config.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.entities.DinaComponent;
import ca.gc.aafc.collection.api.entities.ExtensionValue;
import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Extension;
import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Field;
import lombok.NonNull;

public class BaseExtensionValueValidator implements Validator {

  public static final String NO_MATCH_KEY_VERSION = "validation.constraint.violation.noMatchKeyVersion";
  public static final String NO_MATCH_FIELD_KEY = "validation.constraint.violation.noMatchFieldKey";
  public static final String NO_MATCH_ACCEPTED_VALUE = "validation.constraint.violation.noMatchAcceptedValue";
  public static final String INCORRECT_DINA_COMPONENT = "validation.constraint.violation.incorrectDinaComponent";
  public static final String BLANK_VALUE = "validation.constraint.violation.valueBlank";

  private final DinaComponent componentType;
  private final MessageSource messageSource;
  private final CollectionExtensionConfiguration configuration;

  /**
   *
   * @param componentType used to validate that the extension field used in matching the validator scope
   * @param messageSource
   * @param configuration
   */
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
    handleValidation(errors, extensionValue);
  }

  /**
   * Check if the key and version match. Once a match is found, the field key is searched to ensure
   * it exists within the configuration.
   * 
   * @param errors if any validation problems occur, errors will be reported here.
   * @param extensionValue extension value being validated against.
   */
  private void handleValidation(Errors errors, ExtensionValue extensionValue) {

    for (Extension extension : configuration.getExtension().values()) {
      // First, check if the extension key matches
      if (extension.getKey().equals(extensionValue.getExtKey())) {
        // Check field key
        checkExtensionFieldKey(errors, extensionValue, extension);
        // Check dinaComponent
        checkExtensionConfigurationComponent(errors, extension.getFieldByKey(extensionValue.getExtFieldKey()));
        // make sure there is a value
        checkNotBlankValue(errors, extensionValue);

        // only run the last check if there is no errors
        if (!errors.hasErrors()) {
          checkExtensionConfigurationAcceptedValues(errors, extensionValue,
              extension.getFieldByKey(extensionValue.getExtFieldKey()));
        }
        return;
      }
    }

    String errorMessage = getMessageForKey(
      NO_MATCH_KEY_VERSION, 
      extensionValue.getExtKey());
    errors.rejectValue("extKey", NO_MATCH_KEY_VERSION, errorMessage);
  }

  /**
   * Checks if the provided value is considered blank.
   * Blank refers to the definition of Apache StringUtils isBlank.
   * @param errors
   * @param extensionValue
   */
  private void checkNotBlankValue(Errors errors, ExtensionValue extensionValue) {
    if(StringUtils.isBlank(extensionValue.getValue())) {
      errors.rejectValue(ExtensionValue.VALUE_KEY_NAME, BLANK_VALUE, getMessageForKey(BLANK_VALUE));
    }
  }

  /**
   * Checks to see if the field key exists within the extension set. If no match is found,
   * add the error to errors.
   * 
   * @param errors if any validation problems occur, errors will be reported here.
   * @param extensionValue extension value being validated against.
   * @param extension the extension set of values that that matches the key and version.
   */
  private void checkExtensionFieldKey(Errors errors, ExtensionValue extensionValue, Extension extension) {
    if(!extension.containsKey(extensionValue.getExtFieldKey())) {
      String errorMessage = getMessageForKey(NO_MATCH_FIELD_KEY, extensionValue.getExtKey(),
              extensionValue.getExtFieldKey());
      errors.rejectValue(ExtensionValue.FIELD_KEY_NAME, NO_MATCH_FIELD_KEY, errorMessage);
    }
  }

  /**
   * Looks at the dinaComponent to ensure it's allowed to be used where the
   * extension value is being saved. When the validator is set up, a dinaComponent is setup
   * 
   * @param errors if any validation problems occur, errors will be reported here.
   * @param field the field that was matched with the term.
   */
  private void checkExtensionConfigurationComponent(Errors errors, Field field) {
    if (field == null) {
      return;
    }

    DinaComponent extensionComponent = DinaComponent.valueOf(field.getDinaComponent());

    if (!extensionComponent.equals(componentType)) {
      String errorMessage = getMessageForKey(INCORRECT_DINA_COMPONENT, componentType, field.getDinaComponent());
      errors.rejectValue(ExtensionValue.FIELD_KEY_NAME, INCORRECT_DINA_COMPONENT, errorMessage);
    }
  }

  /**
   * Checks if the field contains any accepted values. If it does, the extension value provided must
   * match one of those accepted values.
   * 
   * @param errors if any validation problems occur, errors will be reported here.
   * @param extensionValue extension value being validated against.
   * @param field the field that was matched with the term.
   */
  private void checkExtensionConfigurationAcceptedValues(Errors errors, ExtensionValue extensionValue, Field field) {
    // Check if the field has accepted values, if it does, check that the value provided matches a accepted value.
    if (field.getAcceptedValues() == null || field.isAcceptedValues(extensionValue.getValue())) {
      return;
    }

    String errorMessage = getMessageForKey(
      NO_MATCH_ACCEPTED_VALUE, 
      extensionValue.getValue(),
      String.join(", ", field.getAcceptedValues()));
    errors.rejectValue(ExtensionValue.FIELD_KEY_NAME, NO_MATCH_ACCEPTED_VALUE, errorMessage);
  }

  private String getMessageForKey(String key, Object... objects) {
    return messageSource.getMessage(key, objects, LocaleContextHolder.getLocale());
  }
  
}
