package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidatorAssemblage;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;

@Service
public class AssemblageService extends DefaultDinaService<Assemblage> {

  private final CollectionManagedAttributeValueValidatorAssemblage
    managedAttributeValueValidatorAssemblage;

  public AssemblageService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv,
                           @NonNull CollectionManagedAttributeValueValidatorAssemblage managedAttributeValueValidatorAssemblage) {
    super(baseDAO, sv);
    this.managedAttributeValueValidatorAssemblage = managedAttributeValueValidatorAssemblage;
  }

  @Override
  protected void preCreate(Assemblage entity) {
    // allow user provided UUID
    if (entity.getUuid() == null) {
      entity.setUuid(UUIDHelper.generateUUIDv7());
    }
    entity.setGroup(standardizeGroupName(entity));
  }

  @Override
  public void validateBusinessRules(Assemblage entity) {
    validateManagedAttribute(entity);
  }

  private void validateManagedAttribute(Assemblage entity) {
    managedAttributeValueValidatorAssemblage.validate(entity, entity.getManagedAttributes());
  }
}
