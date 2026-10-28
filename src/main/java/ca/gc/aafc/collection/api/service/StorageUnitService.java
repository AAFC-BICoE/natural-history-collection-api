package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidatorStorageUnit;
import ca.gc.aafc.dina.dto.HierarchicalObject;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.messaging.DinaEventPublisher;
import ca.gc.aafc.dina.messaging.EntityChanged;
import ca.gc.aafc.dina.service.MessageProducingService;
import ca.gc.aafc.dina.service.PostgresHierarchicalDataService;
import ca.gc.aafc.dina.util.UUIDHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NonNull;

@Service
public class StorageUnitService extends MessageProducingService<StorageUnit> {

  private final StorageUnitTypeService storageUnitTypeService;
  private final PostgresHierarchicalDataService postgresHierarchicalDataService;
  private final CollectionManagedAttributeValueValidatorStorageUnit managedAttributeValueValidator;

  public StorageUnitService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv,
    @NonNull PostgresHierarchicalDataService postgresHierarchicalDataService,
    @NonNull StorageUnitTypeService storageUnitTypeService,
    @NonNull CollectionManagedAttributeValueValidatorStorageUnit managedAttributeValueValidator,
    DinaEventPublisher<EntityChanged> eventPublisher
  ) {
    super(baseDAO, sv, StorageUnitDto.TYPENAME, eventPublisher);
    this.postgresHierarchicalDataService = postgresHierarchicalDataService;
    this.storageUnitTypeService = storageUnitTypeService;
    this.managedAttributeValueValidator = managedAttributeValueValidator;
  }

  @Override
  protected void preCreate(StorageUnit entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
    entity.setGroup(standardizeGroupName(entity));
    cleanupManagedAttributes(entity);
  }

  @Override
  public void preUpdate(StorageUnit entity) {
    cleanupManagedAttributes(entity);
  }

  @Override
  public void validateBusinessRules(StorageUnit entity) {
    managedAttributeValueValidator.validate(entity, entity.getManagedAttributes());
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
  public StorageUnit handleOptionalFields(StorageUnit entity, Map<String, List<String>> optionalFields) {
    if (optionalFields.getOrDefault(StorageUnitDto.TYPENAME, List.of()).contains(StorageUnit.HIERARCHY_PROP_NAME)) {
      setHierarchy(entity);
    }
    if (optionalFields.getOrDefault(StorageUnitDto.TYPENAME, List.of()).contains(StorageUnit.CHILDREN_PROP_NAME)) {
      // lazy loading
      entity.getStorageUnitChildren();
    }
    return entity;
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

  private void cleanupManagedAttributes(StorageUnit entity) {
    Map<String, String> values = entity.getManagedAttributes();
    if (values == null) {
      entity.setManagedAttributes(Map.of());
      return;
    }
    Map<String, String> cleanedValues = new HashMap<>(values);
    cleanedValues.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isBlank());
    entity.setManagedAttributes(cleanedValues);
  }
}
