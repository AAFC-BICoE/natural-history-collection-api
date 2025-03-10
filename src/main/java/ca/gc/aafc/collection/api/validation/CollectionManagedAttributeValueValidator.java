package ca.gc.aafc.collection.api.validation;

import javax.inject.Named;

import ca.gc.aafc.dina.entity.DinaEntity;
import ca.gc.aafc.dina.validation.ValidationContext;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.service.ManagedAttributeService;
import ca.gc.aafc.dina.validation.ManagedAttributeValueValidator;

import lombok.NonNull;
import org.springframework.validation.Errors;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Component
public class CollectionManagedAttributeValueValidator extends ManagedAttributeValueValidator<CollectionManagedAttribute> {

  private static final String INVALID_VALIDATION_CONTEXT_KEY = "managedAttribute.validation.context.invalid";
  private static final String COMPONENT_FIELD_NAME = "managedAttributeComponent";

  private final ManagedAttributeService<CollectionManagedAttribute> dinaService;

  public CollectionManagedAttributeValueValidator(
      @Named("validationMessageSource") MessageSource baseMessageSource, // from dina-base
      @NonNull ManagedAttributeService<CollectionManagedAttribute> dinaService) {
    super(baseMessageSource, dinaService);
    this.dinaService = dinaService;
  }

  public <D extends DinaEntity> void validate(D entity, Map<String, String> managedAttributes, CollectionManagedAttributeValidationContext context) {
    super.validate(entity, managedAttributes, context);
  }

  /**
   * override base class version to also add a restriction on the component since the uniqueness
   * for CollectionManagedAttribute is key + component.
   * @param keys
   * @param validationContext
   * @return
   */
  @Override
  protected Map<String, CollectionManagedAttribute> findAttributesForValidation(
      Set<String> keys, ValidationContext validationContext) {
    return dinaService.findAttributesForKeys(keys, Pair.of(COMPONENT_FIELD_NAME, validationContext.getValue()));
  }

  @Override
  protected boolean preValidateValue(CollectionManagedAttribute managedAttributeDefinition,
      String value, Errors errors, ValidationContext validationContext) {

    // expected context based on the component
    CollectionManagedAttributeValidationContext expectedContext =
        CollectionManagedAttributeValidationContext.from(managedAttributeDefinition.getManagedAttributeComponent());

    if (!expectedContext.equals(validationContext)) {
      errors.reject(INVALID_VALIDATION_CONTEXT_KEY, getMessageForKey(INVALID_VALIDATION_CONTEXT_KEY,
              Objects.toString(validationContext), expectedContext.toString()));
      return false;
    }
    return true;
  }

  /**
   * Wrapper class to expose {@link ca.gc.aafc.collection.api.entities.CollectionManagedAttribute.ManagedAttributeComponent} as
   * {@link ValidationContext}.
   */
  public static final class CollectionManagedAttributeValidationContext implements ValidationContext {
    // make sure to only keep 1 instance per enum value
    private static final EnumMap<CollectionManagedAttribute.ManagedAttributeComponent, CollectionManagedAttributeValidationContext> INSTANCES =
            new EnumMap<>(CollectionManagedAttribute.ManagedAttributeComponent.class);
    private final CollectionManagedAttribute.ManagedAttributeComponent managedAttributeComponent;

    /**
     * Use {@link #from(CollectionManagedAttribute.ManagedAttributeComponent)} method
     * @param managedAttributeComponent
     */
    private CollectionManagedAttributeValidationContext(CollectionManagedAttribute.ManagedAttributeComponent managedAttributeComponent) {
      this.managedAttributeComponent = managedAttributeComponent;
    }

    public static CollectionManagedAttributeValidationContext from(CollectionManagedAttribute.ManagedAttributeComponent managedAttributeComponent) {
      return INSTANCES.computeIfAbsent(managedAttributeComponent, CollectionManagedAttributeValidationContext::new);
    }

    @Override
    public String toString() {
      return managedAttributeComponent.toString();
    }

    @Override
    public Object getValue() {
      return managedAttributeComponent;
    }
  }

}
