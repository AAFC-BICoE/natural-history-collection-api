package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.validation.MaterialSampleValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.validation.SmartValidator;

@Service
public class MaterialSampleService extends DefaultDinaService<MaterialSample> {

  private final MaterialSampleValidator materialSampleValidator;

  public MaterialSampleService(@NonNull BaseDAO baseDAO,
      @NonNull SmartValidator sv,
      @NonNull MaterialSampleValidator materialSampleValidator) {
    super(baseDAO, sv);
    this.materialSampleValidator = materialSampleValidator;
  }

  @Override
  protected void preCreate(MaterialSample entity) {
    entity.setUuid(UUID.randomUUID());
  }

  @Override
  public void validateBusinessRules(MaterialSample entity) {
    applyBusinessRule(entity, materialSampleValidator);
  }

}
