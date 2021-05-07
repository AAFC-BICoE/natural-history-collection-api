package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import ca.gc.aafc.collection.api.entities.PreparationType;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

public class PreparationTypeService extends DefaultDinaService<PreparationType> {
  public PreparationTypeService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(PreparationType entity) {
    entity.setUuid(UUID.randomUUID());
  }
  
}
