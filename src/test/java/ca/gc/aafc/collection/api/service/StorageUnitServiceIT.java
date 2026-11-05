package ca.gc.aafc.collection.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionControlledVocabularyItemFactory;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitFactory;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;
import jakarta.validation.ValidationException;
import java.util.Map;
import org.junit.jupiter.api.Test;

class StorageUnitServiceIT extends CollectionModuleBaseIT {

  @Test
  void managedAttributes_ArePersisted() {
    CollectionControlledVocabularyItem managedAttribute = createManagedAttribute(
      TypedVocabularyElement.VocabularyElementType.STRING);
    StorageUnit storageUnit = StorageUnitFactory.newStorageUnit()
      .managedAttributes(Map.of(managedAttribute.getKey(), "Carabidae"))
      .build();

    storageUnitService.create(storageUnit);
    storageUnitService.flush();
    storageUnitService.detach(storageUnit);

    StorageUnit persisted = storageUnitService.findOne(storageUnit.getUuid(), StorageUnit.class);
    assertEquals("Carabidae", persisted.getManagedAttributes().get(managedAttribute.getKey()));
  }

  @Test
  void managedAttributes_AreValidated() {
    CollectionControlledVocabularyItem managedAttribute = createManagedAttribute(
      TypedVocabularyElement.VocabularyElementType.INTEGER);
    StorageUnit storageUnit = StorageUnitFactory.newStorageUnit()
      .managedAttributes(Map.of(managedAttribute.getKey(), "not-an-integer"))
      .build();

    assertThrows(ValidationException.class, () -> storageUnitService.create(storageUnit));
  }

  private CollectionControlledVocabularyItem createManagedAttribute(
    TypedVocabularyElement.VocabularyElementType type) {
    CollectionControlledVocabularyItem managedAttribute =
      CollectionControlledVocabularyItemFactory.newCollectionManagedAttribute()
        .acceptedValues(null)
        .vocabularyElementType(type)
        .dinaComponent(CollectionVocabularyConfiguration.DinaComponent.STORAGE_UNIT.name())
        .controlledVocabulary(getManagedAttributeControlledVocabularyRef())
        .build();
    collectionControlledVocabularyItemService.create(managedAttribute);
    return managedAttribute;
  }
}
