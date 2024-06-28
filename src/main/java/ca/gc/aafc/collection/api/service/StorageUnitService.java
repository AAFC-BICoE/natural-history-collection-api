package ca.gc.aafc.collection.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.dina.dto.HierarchicalObject;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.jpa.PredicateSupplier;
import ca.gc.aafc.dina.service.MessageProducingService;
import ca.gc.aafc.dina.service.PostgresHierarchicalDataService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;

@Service
public class StorageUnitService extends MessageProducingService<StorageUnit> {

  private final StorageUnitTypeService storageUnitTypeService;
  private final PostgresHierarchicalDataService postgresHierarchicalDataService;

  public StorageUnitService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv,
    @NonNull PostgresHierarchicalDataService postgresHierarchicalDataService,
    @NonNull StorageUnitTypeService storageUnitTypeService,
    ApplicationEventPublisher eventPublisher
  ) {
    super(baseDAO, sv, StorageUnitDto.TYPENAME, eventPublisher);
    this.postgresHierarchicalDataService = postgresHierarchicalDataService;
    this.storageUnitTypeService = storageUnitTypeService;
  }

  @Override
  protected void preCreate(StorageUnit entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
  }

  @Override
  public StorageUnit update(StorageUnit entity) {
    StorageUnit updatedEntity = super.update(entity);
    if (updatedEntity.getParentStorageUnit() != null) {
      // detach the parent to make sure it reloads its children list
      detach(updatedEntity.getParentStorageUnit());
    }
    return updatedEntity;
  }

  @Override
  public <T> List<T> findAll(
    @NonNull Class<T> entityClass,
    @NonNull PredicateSupplier<T> where,
    BiFunction<CriteriaBuilder, Root<T>, List<Order>> orderBy,
    int startIndex,
    int maxResult,
    @NonNull Set<String> includes,
    @NonNull Set<String> relationships
  ) {
    List<T> all = super.findAll(entityClass, where, orderBy, startIndex, maxResult, includes, relationships);
    if (includes.contains(StorageUnit.HIERARCHY_PROP_NAME) && CollectionUtils.isNotEmpty(all) && entityClass == StorageUnit.class) {
      all.forEach(t -> {
        if (t instanceof StorageUnit) {
          setHierarchy((StorageUnit) t);
        }
      });
    }
    return all;
  }

  private void setHierarchy(StorageUnit unit) {
    List<HierarchicalObject> hierarchicalObjects = postgresHierarchicalDataService.getHierarchyWithType(
      unit.getId(),
      StorageUnit.TABLE_NAME,
      StorageUnit.ID_COLUMN_NAME,
      StorageUnit.UUID_COLUMN_NAME,
      StorageUnit.PARENT_ID_COLUMN_NAME,
      StorageUnit.NAME_COLUMN_NAME,
      StorageUnit.TYPE_COLUMN_NAME);
    List<StorageHierarchicalObject> storageHierarchicalObjects = new ArrayList<>();
    for (HierarchicalObject hObject : hierarchicalObjects) {
      StorageHierarchicalObject storageHierarchicalObject = new StorageHierarchicalObject();
      storageHierarchicalObject.setHierarchicalObject(hObject);
      if (hObject.getType() != null) {
        StorageUnitType storageUnitType = storageUnitTypeService.findOneById(Integer.parseInt(hObject.getType()), StorageUnitType.class);
        storageHierarchicalObject.setTypeName(storageUnitType.getName());
        storageHierarchicalObject.setTypeUuid(storageUnitType.getUuid());
      }
      storageHierarchicalObjects.add(storageHierarchicalObject);
    }
    unit.setHierarchy(storageHierarchicalObjects);
  }
}
