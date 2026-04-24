package ca.gc.aafc.collection.api.service;

import lombok.NonNull;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.Association;
import ca.gc.aafc.collection.api.validation.AssociationValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.util.UUIDHelper;

@Service
public class AssociationService extends DefaultDinaService<Association> {

  private final AssociationValidator associationValidator;

  public AssociationService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv,
                            @NonNull AssociationValidator associationValidator) {
    super(baseDAO, sv);
    this.associationValidator = associationValidator;
  }

  @Override
  protected void preCreate(Association entity) {
    // allow user provided UUID
    if (entity.getUuid() == null) {
      entity.setUuid(UUIDHelper.generateUUIDv7());
    }
  }

  @Override
  public void validateBusinessRules(Association entity) {
    applyBusinessRule(entity, associationValidator);
  }
}
