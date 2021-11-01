package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import ca.gc.aafc.collection.api.entities.Collection;
import lombok.NonNull;

@Component
public class CollectionValidator implements Validator {

  private final MessageSource messageSource;

  public CollectionValidator(MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  public static final String VALID_PARENT_HAS_NO_PARENT = "validation.constraint.violation.parentHasNoParent";

  @Override
  public boolean supports(@NonNull Class<?> clazz) {
    return Collection.class.isAssignableFrom(clazz);
  }

  @Override
  public void validate(@NonNull Object target, @NonNull Errors errors) {
    if (!supports(target.getClass())) {
      throw new IllegalArgumentException("Collection not supported for class " + target.getClass());
    }
    Collection collection = (Collection) target;
    checkParentHasNoParent(errors, collection);
    
  }

  private void checkParentHasNoParent(Errors errors, Collection collection) {
    if (collection.getParentCollection() != null && 
      collection.getParentCollection().getParentCollection() != null) {
      String errorMessage = getMessage(VALID_PARENT_HAS_NO_PARENT);
      errors.rejectValue("parentCollection", VALID_PARENT_HAS_NO_PARENT, errorMessage);
    }
  }

  private String getMessage(String key) {
    return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
  }
  
}
