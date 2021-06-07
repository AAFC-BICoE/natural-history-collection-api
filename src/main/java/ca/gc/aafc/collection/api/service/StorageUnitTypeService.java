package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StorageUnitTypeService extends DefaultDinaService<StorageUnitType> {
  public StorageUnitTypeService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(StorageUnitType entity) {
    entity.setUuid(UUID.randomUUID());
  }
}