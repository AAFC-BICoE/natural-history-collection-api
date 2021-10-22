package ca.gc.aafc.collection.api.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.Association;
import lombok.NonNull;

@Component
public class AssociationValidator implements Validator {

  private final CollectionVocabularyConfiguration collectionVocabularyConfiguration;

  public static final String ASSOCIATION_TYPE_NOT_IN_VOCABULARY = "validation.constraint.violation.associationTypeNotInVocabulary";
  
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
    
  }

  private void validateAssociationType(Errors errors, Association association) {
    if (StringUtils.isNotBlank(association.getAssociationType())) {
      if (!collectionVocabularyConfiguration.getVocabulary().get("associationType")
      .stream().filter(o -> o.getName().equals(association.getAssociationType())).findFirst().isPresent()) {
        String errorMessage = getMessage(ASSOCIATION_TYPE_NOT_IN_VOCABULARY);
        errors.rejectValue("associationType", ASSOCIATION_TYPE_NOT_IN_VOCABULARY, errorMessage);
      }
    }
  }

  private String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
}
