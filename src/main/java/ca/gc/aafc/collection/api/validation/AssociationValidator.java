package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.dina.service.ControlledVocabularyItemService;
import ca.gc.aafc.dina.validation.BaseControlledVocabularyValueValidator;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

import static ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration.ASSOCIATION_TYPE_VOCAB_UUID;

import jakarta.inject.Named;
import java.util.Objects;
import lombok.NonNull;

@Component
public class AssociationValidator extends BaseControlledVocabularyValueValidator<CollectionControlledVocabularyItem> {

  public static final String ASSOCIATED_WITH_SELF_ERROR_KEY = "validation.constraint.violation.association.associatedWithSelf";
  private final MessageSource collMessageSource;

  public AssociationValidator(@Named("validationMessageSource") MessageSource validationMessageSource,
                              MessageSource messageSource,
                              @NonNull ControlledVocabularyItemService<CollectionControlledVocabularyItem> vocabItemService) {
    super(validationMessageSource, vocabItemService);
    this.collMessageSource = messageSource;
  }

  public void validate(Association entity, String associationType) {
    Errors errors = ValidationErrorsHelper.newErrorsObject(entity);
    validateAssociationNotSelf(entity, errors);
    validateKey(associationType, () -> vocabItemService
        .findOneByKey(associationType, ASSOCIATION_TYPE_VOCAB_UUID), errors);
  }

  private void validateAssociationNotSelf(Association association, Errors errors) {
    // out of scope for this validation method
    if (association.getAssociatedSample() == null || association.getSample() == null) {
      return;
    }

    if (Objects.equals(association.getAssociatedSample().getUuid(), association.getSample().getUuid())) {
      String errorMessage = collMessageSource.getMessage(ASSOCIATED_WITH_SELF_ERROR_KEY, null, LocaleContextHolder.getLocale());
      errors.rejectValue("associationType", ASSOCIATED_WITH_SELF_ERROR_KEY, errorMessage);
      ValidationErrorsHelper.errorsToValidationException(errors);
    }
  }
}
