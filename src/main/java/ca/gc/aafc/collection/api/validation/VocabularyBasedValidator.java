package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.dina.validation.DinaBaseValidator;

import java.util.List;
import java.util.Optional;

/**
 * Based, package-protected class used to validate values against specific vocabulary.
 * @param <T>
 */
abstract class VocabularyBasedValidator<T> extends DinaBaseValidator<T> {

  public static final String VALUE_NOT_IN_VOCABULARY = "validation.constraint.violation.notInVocabulary";

  VocabularyBasedValidator(MessageSource messageSource, Class<T> supportedClass) {
    super(supportedClass, messageSource);
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
}
