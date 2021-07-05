package ca.gc.aafc.collection.api.validation;

import javax.inject.Named;

import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.validation.ValidationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.service.ManagedAttributeService;
import ca.gc.aafc.dina.validation.ManagedAttributeValueValidator;

import lombok.NonNull;
import org.springframework.validation.Errors;

import java.util.Map;
import java.util.Optional;

@Component
public class CollectionManagedAttributeValueValidator extends ManagedAttributeValueValidator<CollectionManagedAttribute> {

  private static final String INVALID_VALIDATION_CONTEXT_KEY = "managedAttribute.validation.context.invalid";

  private final MessageSource messageSource;

  public CollectionManagedAttributeValueValidator(
      @Named("validationMessageSource") MessageSource baseMessageSource, // from dina-base
      @NonNull MessageSource messageSource,
      @NonNull ManagedAttributeService<CollectionManagedAttribute> dinaService) {
    super(baseMessageSource, dinaService);
    this.messageSource = messageSource;
  }

  public <D extends DinaEntity> void validate(D entity, Map<String, String> managedAttributes, CollectionManagedAttributeValidationContext context) {
    super.validate(entity, managedAttributes, context);
  }

  @Override
  protected boolean preValidateValue(CollectionManagedAttribute managedAttributeDefinition,
      String value, Errors errors, ValidationContext validationContext) {

    // expected context based on the component
    Optional<CollectionManagedAttributeValidationContext> expectedContext =
        CollectionManagedAttributeValidationContext.from(managedAttributeDefinition.getManagedAttributeComponent());

    // context received from the validate method
    Optional<CollectionManagedAttributeValidationContext> collValidationContext =
        Optional.ofNullable(validationContext).map( vc -> (CollectionManagedAttributeValidationContext)validationContext);

    if(!expectedContext.equals(collValidationContext)) {
      errors.reject(INVALID_VALIDATION_CONTEXT_KEY, getMessageForKey(INVALID_VALIDATION_CONTEXT_KEY,
          collValidationContext.map(Enum::toString).orElse("?"),
          expectedContext.map(Enum::toString).orElse("?")));
      return false;
    }
    return true;
  }

  public enum CollectionManagedAttributeValidationContext implements ValidationContext {
    COLLECTING_EVENT(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT),
    MATERIAL_SAMPLE(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE);

    CollectionManagedAttributeValidationContext(CollectionManagedAttribute.ManagedAttributeComponent managedAttributeComponent) {
      this.managedAttributeComponent = managedAttributeComponent;
    }

    private final CollectionManagedAttribute.ManagedAttributeComponent managedAttributeComponent;

    public static Optional<CollectionManagedAttributeValidationContext> from(CollectionManagedAttribute.ManagedAttributeComponent managedAttributeComponent) {
      for(CollectionManagedAttributeValidationContext curr : values()) {
        if(curr.managedAttributeComponent == managedAttributeComponent) {
          return Optional.of(curr);
        }
      }
      return Optional.empty();
    }
  }

  private String getMessageForKey(String key, Object... objects) {
    return messageSource.getMessage(key, objects, LocaleContextHolder.getLocale());
  }

}
