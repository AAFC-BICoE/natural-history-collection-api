package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.PreparationType;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;
import org.springframework.validation.SmartValidator;

@Service
public class PreparationTypeService extends DefaultDinaService<PreparationType> {
  public PreparationTypeService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
  }

  @Override
  protected void preCreate(PreparationType entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
  }
  
}
