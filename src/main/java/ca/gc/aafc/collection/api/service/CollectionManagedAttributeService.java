package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.ManagedAttributeService;
import ca.gc.aafc.dina.service.PostgresJsonbService;
import io.crnk.core.exception.BadRequestException;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import java.util.UUID;

@Service
public class CollectionManagedAttributeService extends ManagedAttributeService<CollectionManagedAttribute> {

  public static final String MANAGED_ATTRIBUTES_COL_NAME = "managed_attributes";
  public static final String COLLECTING_EVENT_TABLE_NAME = "collecting_event";
  public static final String MATERIAL_SAMPLE_TABLE_NAME = "material_sample";
  private final PostgresJsonbService jsonbService;

  public CollectionManagedAttributeService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv,
    @NonNull PostgresJsonbService postgresJsonbService
  ) {
    super(baseDAO, sv, CollectionManagedAttribute.class);
    this.jsonbService = postgresJsonbService;
  }

  @Override
  protected void preCreate(CollectionManagedAttribute entity) {
    entity.setUuid(UUID.randomUUID());
    super.preCreate(entity);
  }

  @Override
  protected void preDelete(CollectionManagedAttribute entity) {
    switch (entity.getManagedAttributeComponent()) {
      case COLLECTING_EVENT:
        checkKeysFor(entity.getKey(), COLLECTING_EVENT_TABLE_NAME);
        break;
      case MATERIAL_SAMPLE:
        checkKeysFor(entity.getKey(), MATERIAL_SAMPLE_TABLE_NAME);
        break;
    }
  }

  private void checkKeysFor(String key, String tableName) {
    Integer countFirstLevelKeys = jsonbService.countFirstLevelKeys(
      tableName, CollectionManagedAttributeService.MANAGED_ATTRIBUTES_COL_NAME, key);
    if (countFirstLevelKeys > 0) {
      throw new BadRequestException("Managed attribute key: " + key + ", is currently in use.");
    }
  }
}
