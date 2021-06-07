package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

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

  @Override
  protected void preUpdate(StorageUnit entity) {
    linkParentAssociation(entity);
    resolveChildAssociations(entity);
  }

  @Override
  protected void preDelete(StorageUnit entity) {
    unlinkParent(entity);
    unlinkChildren(entity);
  }

  private void resolveChildAssociations(StorageUnit entity) {
    if (CollectionUtils.isEmpty(entity.getStorageUnitChildren())) {
      return;
    }

    Map<UUID, StorageUnit> incoming = entity.getStorageUnitChildren().stream()
      .collect(Collectors.toMap(StorageUnit::getUuid, Function.identity()));
    Map<UUID, StorageUnit> currentChildren = findStorageUnitsWithParent(entity);

    currentChildren.forEach((uuid, storageUnit) -> {
      if (!incoming.containsKey(uuid)) {
        storageUnit.setParentStorageUnit(null);
      }
    });
    linkChildAssociations(entity);
  }

  private Map<UUID, StorageUnit> findStorageUnitsWithParent(StorageUnit parent) {
    return this.findAll(
      StorageUnit.class,
      (criteriaBuilder, storageUnitRoot) -> new Predicate[]{
        criteriaBuilder.equal(storageUnitRoot.get("parentStorageUnit"), parent)
      }, null, 0, Integer.MAX_VALUE
    ).stream().collect(Collectors.toMap(StorageUnit::getUuid, Function.identity()));
  }

  private void unlinkChildren(StorageUnit entity) {
    if (CollectionUtils.isNotEmpty(entity.getStorageUnitChildren())) {
      entity.getStorageUnitChildren().forEach(c -> c.setParentStorageUnit(null));
    }
  }

  private void unlinkParent(StorageUnit entity) {
    StorageUnit parentStorageUnit = entity.getParentStorageUnit();
    if (parentStorageUnit != null && CollectionUtils.isNotEmpty(parentStorageUnit.getStorageUnitChildren())) {
      parentStorageUnit.getStorageUnitChildren().removeIf(c -> entity.getUuid().equals(c.getUuid()));
    }
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
