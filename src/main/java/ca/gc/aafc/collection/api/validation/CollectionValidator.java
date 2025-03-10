package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.dina.validation.DinaBaseValidator;

@Component
public class CollectionValidator extends DinaBaseValidator<Collection> {

  public static final String VALID_PARENT_HAS_NO_PARENT = "validation.constraint.violation.parentHasNoParent";

  public CollectionValidator(MessageSource messageSource) {
    super(Collection.class, messageSource);
  }

  @Override
  public void validateTarget(Collection target, Errors errors) {
    checkParentHasNoParent(target, errors);
  }

  private void checkParentHasNoParent(Collection collection, Errors errors) {
    if (collection.getParentCollection() != null && 
      collection.getParentCollection().getParentCollection() != null) {
      String errorMessage = getMessage(VALID_PARENT_HAS_NO_PARENT);
      errors.rejectValue("parentCollection", VALID_PARENT_HAS_NO_PARENT, errorMessage);
    }
  }
}
