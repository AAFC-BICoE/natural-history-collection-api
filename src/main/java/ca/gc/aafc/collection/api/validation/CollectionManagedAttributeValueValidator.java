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
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.NonNull;

@Component
public class CollectionManagedAttributeValueValidator extends ManagedAttributeValueValidator<CollectionManagedAttribute> {

  public CollectionManagedAttributeValueValidator(
      @Named("validationMessageSource") MessageSource messageSource,
      @NonNull ManagedAttributeService<CollectionManagedAttribute> dinaService) {
    super(messageSource, dinaService);
  }

  @Override
  @SuppressFBWarnings(value = "RCN_REDUNDANT_NULLCHECK_WOULD_HAVE_BEEN_A_NPE")
  public <D extends DinaEntity> void validate(D entity, Map<String, String> managedAttributes) {
    Errors errors = ValidationErrorsHelper.newErrorsObject(entity);
    validate(managedAttributes, errors);

    if (!errors.hasErrors()) {
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
