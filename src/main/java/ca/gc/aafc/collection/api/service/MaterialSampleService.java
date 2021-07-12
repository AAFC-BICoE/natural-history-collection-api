package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;
import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidator;
import ca.gc.aafc.collection.api.validation.MaterialSampleValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.validation.Errors;
import org.springframework.validation.SmartValidator;

@Service
public class MaterialSampleService extends DefaultDinaService<MaterialSample> {

  private final MaterialSampleValidator materialSampleValidator;
  private final CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator;
  private final SmartValidator validator;


  public MaterialSampleService(
      @NonNull BaseDAO baseDAO,
      @NonNull SmartValidator sv,
      @NonNull MaterialSampleValidator materialSampleValidator,
      @NonNull CollectionManagedAttributeValueValidator collectionManagedAttributeValueValidator) {
    super(baseDAO, sv);
    this.materialSampleValidator = materialSampleValidator;
    this.collectionManagedAttributeValueValidator = collectionManagedAttributeValueValidator;
    this.validator = sv;
  }

  @Override
  protected void preCreate(MaterialSample entity) {
    entity.setUuid(UUID.randomUUID());
  }

  @Override
  public void validateBusinessRules(MaterialSample entity) {
    applyBusinessRule(entity, materialSampleValidator);
    validateManagedAttribute(entity);

    Determination determination = entity.getDetermination();
    if(determination != null) {
      Errors errors = ValidationErrorsHelper.newErrorsObject(entity);
      validator.validate(determination, errors);
      ValidationErrorsHelper.errorsToValidationException(errors);
    }
  }

  private void validateManagedAttribute(MaterialSample entity) {
    collectionManagedAttributeValueValidator.validate(entity, entity.getManagedAttributes(),
        CollectionManagedAttributeValueValidator.CollectionManagedAttributeValidationContext.MATERIAL_SAMPLE);
  }

}
