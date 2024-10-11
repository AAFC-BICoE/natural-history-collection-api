package ca.gc.aafc.collection.api.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration.CollectionVocabularyElement;
import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.dina.validation.DinaBaseValidator;

import java.util.Objects;
import java.util.Optional;

@Component
public class AssociationValidator extends DinaBaseValidator<Association> {

  public static final String ASSOCIATED_WITH_SELF_ERROR_KEY = "validation.constraint.violation.association.associatedWithSelf";
  public static final String ASSOCIATION_TYPE_NOT_IN_VOCABULARY = "validation.constraint.violation.associationTypeNotInVocabulary";
  private final CollectionVocabularyConfiguration collectionVocabularyConfiguration;

  public AssociationValidator(
    MessageSource messageSource,
    CollectionVocabularyConfiguration collectionVocabularyConfiguration) {

    super(Association.class, messageSource);
    this.collectionVocabularyConfiguration = collectionVocabularyConfiguration;
  }

  @Override
  public void validateTarget(Association target, Errors errors) {
    validateAssociationType(target, errors);
    validateAssociationNotSelf(target, errors);
  }

  private void validateAssociationNotSelf(Association association, Errors errors) {
    if (Objects.equals(association.getAssociatedSample().getUuid(), association.getSample().getUuid())) {
      String errorMessage = getMessage(ASSOCIATED_WITH_SELF_ERROR_KEY);
      errors.rejectValue("associationType", ASSOCIATED_WITH_SELF_ERROR_KEY, errorMessage);
    }
  }

  private void validateAssociationType(Association association, Errors errors) {
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

}
