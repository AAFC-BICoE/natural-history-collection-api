package ca.gc.aafc.collection.api.validation;

import org.apache.commons.collections4.MapUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.config.TypedVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.validation.TypedVocabularyElementValidator;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Validates that the key(s) used in materialSample.identifier is valid and that the associated value
 * is also valid.
 */
@Component
public class IdentifierTypeValueValidator implements Validator {

  private static final String FIELD_NAME = "identifier";
  private static final String INVALID_KEY_KEY = "validation.identifier.violation.invalidKey";
  private static final String INVALID_VALUE_KEY = "validation.identifier.violation.invalidValue";

  private final MessageSource messageSource;
  private final List<? extends TypedVocabularyElement> identifierType;

  public IdentifierTypeValueValidator(MessageSource messageSource, TypedVocabularyConfiguration typedVocabularyConfiguration) {
    this.messageSource = messageSource;
    identifierType = typedVocabularyConfiguration.getIdentifierType();
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return MaterialSample.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException(
        "IdentifierTypeValueValidator not supported for class " + target.getClass());
    }

    validate((MaterialSample) target, errors);
  }

  public void validate(MaterialSample target, Errors errors) {

    Map<String, String> identifier = target.getIdentifiers();
    if (MapUtils.isEmpty(identifier)) {
      return;
    }

    for (var entry : identifier.entrySet()) {
      Optional<? extends TypedVocabularyElement> typedVocab = findInTypedVocabulary(entry.getKey());
      if (typedVocab.isEmpty()) {
        // error. key not found
        String errorMessage = getMessageForKey(INVALID_KEY_KEY, entry.getKey());
        errors.rejectValue(FIELD_NAME, INVALID_KEY_KEY, errorMessage);
        return;
      }

      if (!TypedVocabularyElementValidator.isValidElement(typedVocab.get(), entry.getValue())) {
        String errorMessage = getMessageForKey(INVALID_VALUE_KEY, entry.getKey(), entry.getValue());
        errors.rejectValue(FIELD_NAME, INVALID_VALUE_KEY, errorMessage);
        return;
      }
    }
  }

  /**
   * Finds the first TypedVocabularyElement where the key is matching the provided key.
   * @param key
   * @return
   */
  protected Optional<? extends TypedVocabularyElement> findInTypedVocabulary(String key) {
    return identifierType.stream().filter(o -> o.getKey().equals(key)).findFirst();
  }

  private String getMessageForKey(String key, Object... objects) {
    return messageSource.getMessage(key, objects, LocaleContextHolder.getLocale());
  }
}
