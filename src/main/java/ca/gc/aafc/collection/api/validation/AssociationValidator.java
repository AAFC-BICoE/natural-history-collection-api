package ca.gc.aafc.collection.api.validation;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration.CollectionVocabularyElement;
import ca.gc.aafc.collection.api.entities.Association;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Objects;
import java.util.Optional;

@Component
public class AssociationValidator implements Validator {

  public static final String ASSOCIATED_WITH_SELF_ERROR_KEY = "validation.constraint.violation.association.associatedWithSelf";
  public static final String ASSOCIATION_TYPE_NOT_IN_VOCABULARY = "validation.constraint.violation.associationTypeNotInVocabulary";
  private final CollectionVocabularyConfiguration collectionVocabularyConfiguration;
  private final MessageSource messageSource;

  public AssociationValidator(
    MessageSource messageSource, 
    CollectionVocabularyConfiguration collectionVocabularyConfiguration
  ) {
    this.messageSource = messageSource;
    this.collectionVocabularyConfiguration = collectionVocabularyConfiguration;
  }

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return Association.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("AssociationValidator not supported for class " + target.getClass());
    }
    Association association = (Association) target;
    validateAssociationType(errors, association);
    validateAssociationNotSelf(errors, association);
  }

  private void validateAssociationNotSelf(Errors errors, Association association) {
    if (Objects.equals(association.getAssociatedSample().getUuid(), association.getSample().getUuid())) {
      String errorMessage = getMessage(ASSOCIATED_WITH_SELF_ERROR_KEY);
      errors.rejectValue("associationType", ASSOCIATED_WITH_SELF_ERROR_KEY, errorMessage);
    }
  }

  private void validateAssociationType(Errors errors, Association association) {
    if (StringUtils.isNotBlank(association.getAssociationType())) {
      Optional<CollectionVocabularyElement> foundAssociationType = collectionVocabularyConfiguration.getVocabulary().get("associationType")
        .stream().filter(o -> o.getName().equalsIgnoreCase(association.getAssociationType())).findFirst();
      if (foundAssociationType.isPresent()) {
        association.setAssociationType(foundAssociationType.get().getName());
      } else {
        String errorMessage = getMessage(ASSOCIATION_TYPE_NOT_IN_VOCABULARY);
        errors.rejectValue("associationType", ASSOCIATION_TYPE_NOT_IN_VOCABULARY, errorMessage);
      }
    }
  }

  private String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
}
