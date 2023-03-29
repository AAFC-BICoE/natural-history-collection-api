package ca.gc.aafc.collection.api.validation;

import java.util.List;
import java.util.Optional;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;

/**
 * Based, package-protected class used to validate values against specific vocabulary.
 * @param <T>
 */
abstract class VocabularyBasedValidator<T> implements Validator {

  public static final String VALUE_NOT_IN_VOCABULARY = "validation.constraint.violation.notInVocabulary";

  private final MessageSource messageSource;
  private final Class<T> supportedClass;

  VocabularyBasedValidator(MessageSource messageSource, Class<T> supportedClass) {
    this.messageSource = messageSource;
    this.supportedClass = supportedClass;
  }

  @Override
  public boolean supports(Class<?> clazz) {
    return supportedClass.isAssignableFrom(clazz);
  }

  @Override
  public void validate(Object target, Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("VocabularyBasedValidator not supported for class " + target.getClass());
    }
    validateVocabularyBasedAttribute(supportedClass.cast(target), errors);
  }

  /**
   * Checks if the provided value matches an entry in vocabularyElement list by comparing it to keys (ignore case).
   * @param value value to validate and standardize
   * @param fieldName used to report error
   * @param vocabularyElements valid elements for the vocabulary
   * @param errors stores the errors
   * @return standardized value of the provided value or the same value if an error occurred
   */
  protected String validateAndStandardizeValueAgainstVocabulary(String value, String fieldName, List<CollectionVocabularyConfiguration.CollectionVocabularyElement> vocabularyElements, Errors errors) {
    Optional<CollectionVocabularyConfiguration.CollectionVocabularyElement> foundVocabularyElement = findInVocabulary(value, vocabularyElements);
    if (foundVocabularyElement.isPresent()) {
      return foundVocabularyElement.get().getKey();
    } else {
      String errorMessage = getMessage(VALUE_NOT_IN_VOCABULARY, fieldName);
      errors.rejectValue(fieldName, VALUE_NOT_IN_VOCABULARY, errorMessage);
    }
    return value;
  }

  /**
   * Finds the first CollectionVocabularyElement where the key is matching (ignore case) the provided value.
   * @param value
   * @param vocabularyElements
   * @return
   */
  protected Optional<CollectionVocabularyConfiguration.CollectionVocabularyElement> findInVocabulary(String value, List<CollectionVocabularyConfiguration.CollectionVocabularyElement> vocabularyElements) {
    return vocabularyElements.stream().filter(o -> o.getKey().equalsIgnoreCase(value)).findFirst();
  }

  protected abstract void validateVocabularyBasedAttribute(T target, Errors errors);

  protected String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
  protected String getMessage(String key, Object... messageArgs) {
    return messageSource.getMessage(key, messageArgs, LocaleContextHolder.getLocale());
  }

}
