package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.ControlledVocabularyItemService;
import ca.gc.aafc.dina.service.PostgresJsonbService;
import ca.gc.aafc.dina.validation.ControlledVocabularyItemValidator;

import java.util.Objects;

@Service
public class CollectionControlledVocabularyItemService extends ControlledVocabularyItemService<CollectionControlledVocabularyItem> {

  public static final String MANAGED_ATTRIBUTES_COL_NAME = "managed_attributes";
  public static final String COLLECTING_EVENT_TABLE_NAME = "collecting_event";
  public static final String MATERIAL_SAMPLE_TABLE_NAME = "material_sample";

  private final PostgresJsonbService jsonbService;

  public CollectionControlledVocabularyItemService(BaseDAO baseDAO, PostgresJsonbService jsonbService,
                                                   SmartValidator smartValidator,
                                                   ControlledVocabularyItemValidator itemValidator) {
    super(baseDAO, smartValidator, CollectionControlledVocabularyItem.class, itemValidator);
    this.jsonbService = jsonbService;
  }

  @Override
  protected void preDelete(CollectionControlledVocabularyItem entity) {

    if (CollectionVocabularyConfiguration.MANAGED_ATTRIBUTE_VOCAB_UUID.equals(
      entity.getControlledVocabulary().getUuid())) {
      checkManagedAttributeUsage(entity);
    }
  }

  private void checkManagedAttributeUsage(CollectionControlledVocabularyItem entity) {
    CollectionVocabularyConfiguration.DinaComponent component =
      CollectionVocabularyConfiguration.DinaComponent.fromString(entity.getDinaComponent());

    Objects.requireNonNull(component);

    switch (component) {
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
          "Unexpected managed attribute component of: " + entity.getDinaComponent());
    }
  }

  private void checkKeysFor(String key, String tableName) {
    Integer countFirstLevelKeys = jsonbService.countFirstLevelKeys(
      tableName, MANAGED_ATTRIBUTES_COL_NAME, key);
    if (countFirstLevelKeys > 0) {
      throw new IllegalStateException("Managed attribute key: " + key + ", is currently in use.");
    }
  }
}
