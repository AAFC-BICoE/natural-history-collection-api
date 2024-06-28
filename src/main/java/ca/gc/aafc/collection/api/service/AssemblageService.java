package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

@Service
public class AssemblageService extends DefaultDinaService<Assemblage> {

  private final CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext validationContext;
  private final CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator;

  public AssemblageService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv,
                           @NonNull CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator) {
    super(baseDAO, sv);
    this.collectionManagedAttributeValueValidator = collectionManagedAttributeValueValidator;
    this.validationContext = CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext
            .from(CollectionManagedAttribute.ManagedAttributeComponent.ASSEMBLAGE);
  }

  @Override
  protected void preCreate(Assemblage entity) {
    // allow user provided UUID
    if (entity.getUuid() == null) {
      entity.setUuid(UUIDHelper.generateUUIDv7());
    }
  }

  @Override
  public void validateBusinessRules(Assemblage entity) {
    validateManagedAttribute(entity);
  }

  private void validateManagedAttribute(Assemblage entity) {
    collectionManagedAttributeValueValidator.validate(entity, entity.getManagedAttributes(), validationContext);
  }
}
