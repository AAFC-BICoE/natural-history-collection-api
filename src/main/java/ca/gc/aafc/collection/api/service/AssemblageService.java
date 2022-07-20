package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class AssemblageService extends DefaultDinaService<Assemblage> {

  private final CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator;

  public AssemblageService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv,
                           @NonNull CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator) {
    super(baseDAO, sv);
    this.collectionManagedAttributeValueValidator = collectionManagedAttributeValueValidator;
  }

  @Override
  protected void preCreate(Assemblage entity) {
    // allow user provided UUID
    if(entity.getUuid() == null) {
      entity.setUuid(UUID.randomUUID());
    }
  }

  @Override
  public void validateBusinessRules(Assemblage entity) {
    validateManagedAttribute(entity);
  }

  private void validateManagedAttribute(Assemblage entity) {
    collectionManagedAttributeValueValidator.validate(entity, entity.getManagedAttributes(),
            CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext.ASSEMBLAGE);
  }
}
