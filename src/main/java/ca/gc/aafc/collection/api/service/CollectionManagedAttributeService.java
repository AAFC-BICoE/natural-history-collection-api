package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.ManagedAttributeService;
import ca.gc.aafc.dina.service.PostgresJsonbService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

// CHECKSTYLE:OFF NoFinalizer
// CHECKSTYLE:OFF SuperFinalize
@Service
public class CollectionManagedAttributeService extends ManagedAttributeService<CollectionManagedAttribute> {

  public static final String MANAGED_ATTRIBUTES_COL_NAME = "managed_attributes";
  public static final String COLLECTING_EVENT_TABLE_NAME = "collecting_event";
  public static final String MATERIAL_SAMPLE_TABLE_NAME = "material_sample";

  private static final String COMPONENT_FIELD_NAME = "managedAttributeComponent";

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
    entity.setUuid(UUIDHelper.generateUUIDv7());
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
      case DETERMINATION:
        break;
      default:
        throw new IllegalStateException(
          "Unexpected managed attribute component of: " + entity.getManagedAttributeComponent());
    }
  }

  public CollectionManagedAttribute findOneByKeyAndComponent(String key,
      CollectionManagedAttribute.ManagedAttributeComponent component) {
    if (StringUtils.isBlank(key) || component == null) {
      return null;
    }
    return findOneByKeyAnd(key, Pair.of(COMPONENT_FIELD_NAME, component));
  }

  private void checkKeysFor(String key, String tableName) {
    Integer countFirstLevelKeys = jsonbService.countFirstLevelKeys(
      tableName, CollectionManagedAttributeService.MANAGED_ATTRIBUTES_COL_NAME, key);
    if (countFirstLevelKeys > 0) {
      throw new IllegalStateException("Managed attribute key: " + key + ", is currently in use.");
    }
  }

  protected final void finalize() {
    // no-op
  }
}
