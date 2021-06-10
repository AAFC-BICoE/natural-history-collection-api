package ca.gc.aafc.collection.api.validation;

import java.util.Map;
import java.util.Optional;

import javax.inject.Named;
import javax.validation.ValidationException;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.service.ManagedAttributeService;
import ca.gc.aafc.dina.validation.ManagedAttributeValueValidator;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;
import lombok.NonNull;

@Component
public class CollectionManagedAttributeValueValidator extends ManagedAttributeValueValidator<CollectionManagedAttribute> {

  public CollectionManagedAttributeValueValidator(
      @Named("validationMessageSource") MessageSource messageSource,
      @NonNull ManagedAttributeService<CollectionManagedAttribute> dinaService) {
    super(messageSource, dinaService);
  }

  /**
   * Validates the managedAttributes attached to the provided entity.
   * @param entity
   * @param managedAttributes
   * @param <D>
   * @throws javax.validation.ValidationException
   */
  public <D extends DinaEntity> void validate(D entity, Map<String, String> managedAttributes) {
    Errors errors = ValidationErrorsHelper.newErrorsObject(entity);
    validate(managedAttributes, errors);

    if (errors == null || !errors.hasErrors()) {
      return;
    }

    Optional<String> errorMsg = errors.getAllErrors()
        .stream()
        .map(ObjectError::getCode)
        .findAny();

    errorMsg.ifPresent(msg -> {
      throw new ValidationException(msg);
    });
  }

}
