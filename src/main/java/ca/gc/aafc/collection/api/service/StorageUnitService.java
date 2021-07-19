package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.validation.StorageUnitValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.jpa.OneToManyDinaService;
import ca.gc.aafc.dina.jpa.OneToManyFieldHandler;
import ca.gc.aafc.dina.jpa.PredicateSupplier;
import ca.gc.aafc.dina.service.HierarchicalObject;
import ca.gc.aafc.dina.service.PostgresHierarchicalDataService;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Service
public class StorageUnitService extends OneToManyDinaService<StorageUnit> {

  private final StorageUnitValidator storageUnitValidator;
  private final StorageUnitTypeService storageUnitTypeService;
  private final PostgresHierarchicalDataService postgresHierarchicalDataService;

  public StorageUnitService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv,
    @NonNull StorageUnitValidator storageUnitValidator,
    @NonNull PostgresHierarchicalDataService postgresHierarchicalDataService,
    @NonNull StorageUnitTypeService storageUnitTypeService
  ) {
    super(baseDAO, sv, List.of(
      new OneToManyFieldHandler<>(
        StorageUnit.class,
        storageUnit -> storageUnit::setParentStorageUnit,
        StorageUnit::getStorageUnitChildren,
        "parentStorageUnit",
        storageUnit -> storageUnit.setParentStorageUnit(null))
    ));
    this.postgresHierarchicalDataService = postgresHierarchicalDataService;
    this.storageUnitValidator = storageUnitValidator;
    this.storageUnitTypeService = storageUnitTypeService;
  }

  @Override
  protected void preCreate(StorageUnit entity) {
    entity.setUuid(UUID.randomUUID());
  }

  @Override
  public void validateBusinessRules(StorageUnit entity) {
    applyBusinessRule(entity, storageUnitValidator);
  }

  @Override
  public <T> List<T> findAll(
    @NonNull Class<T> entityClass,
    @NonNull PredicateSupplier<T> where,
    BiFunction<CriteriaBuilder, Root<T>, List<Order>> orderBy,
    int startIndex,
    int maxResult
  ) {
    List<T> all = super.findAll(entityClass, where, orderBy, startIndex, maxResult);
    if (CollectionUtils.isNotEmpty(all) && entityClass == StorageUnit.class) {
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
      "storage_unit",
      "id",
      "uuid",
      "parent_storage_unit_id",
      "name",
      "storage_unit_type_id");
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
