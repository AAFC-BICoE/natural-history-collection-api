package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.jpa.OneToManyDinaService;
import ca.gc.aafc.dina.jpa.OneToManyFieldHandler;
import ca.gc.aafc.dina.jpa.PredicateSupplier;
import ca.gc.aafc.dina.service.PostgresHierarchicalDataService;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import java.util.function.BiFunction;

@Service
public class StorageUnitService extends OneToManyDinaService<StorageUnit> {

  private final PostgresHierarchicalDataService postgresHierarchicalDataService;

  public StorageUnitService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv,
    @NonNull PostgresHierarchicalDataService postgresHierarchicalDataService
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
  }

  @Override
  protected void preCreate(StorageUnit entity) {
    entity.setUuid(UUID.randomUUID());
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
    unit.setHierarchy(postgresHierarchicalDataService.getHierarchy(
      unit.getId(),
      "storage_unit",
      "id",
      "uuid",
      "parent_storage_unit_id",
      "name"));
  }
}
