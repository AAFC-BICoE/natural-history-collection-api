package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

@Service
public class StorageUnitService extends DefaultDinaService<StorageUnit> {

  public StorageUnitService(@NonNull BaseDAO baseDAO) {
    super(baseDAO);
  }

  @Override
  protected void preCreate(StorageUnit entity) {
    entity.setUuid(UUID.randomUUID());
    linkParentAssociation(entity);
    linkChildAssociations(entity);
  }

  private void linkChildAssociations(StorageUnit entity) {
    if (CollectionUtils.isNotEmpty(entity.getStorageUnitChildren())) {
      entity.getStorageUnitChildren().forEach(c -> c.setParentStorageUnit(entity));
    }
  }

  private void linkParentAssociation(StorageUnit entity) {
    if (entity.getParentStorageUnit() == null) {
      return;
    }

    StorageUnit parent = entity.getParentStorageUnit();
    if (parent.getStorageUnitChildren() == null) {
      parent.setStorageUnitChildren(new ArrayList<>());
    }

    if (parent.getStorageUnitChildren().stream().noneMatch(c -> c.getUuid().equals(entity.getUuid()))) {
      parent.getStorageUnitChildren().add(entity);
    }
  }

}
