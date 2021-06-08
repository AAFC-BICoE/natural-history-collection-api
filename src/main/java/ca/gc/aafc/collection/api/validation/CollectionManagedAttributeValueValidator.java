package ca.gc.aafc.collection.api.validation;

import javax.inject.Named;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.service.ManagedAttributeService;
import ca.gc.aafc.dina.validation.ManagedAttributeValueValidator;
import lombok.NonNull;

@Component
public class CollectionManagedAttributeValueValidator extends ManagedAttributeValueValidator<CollectionManagedAttribute> {

  public CollectionManagedAttributeValueValidator(
      @Named("validationMessageSource") MessageSource messageSource,
      @NonNull ManagedAttributeService<CollectionManagedAttribute> dinaService) {
    super(messageSource, dinaService);
  }
  
}
