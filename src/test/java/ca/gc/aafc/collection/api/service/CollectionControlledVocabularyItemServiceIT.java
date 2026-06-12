package ca.gc.aafc.collection.api.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabulary;
import ca.gc.aafc.collection.api.entities.CollectionControlledVocabularyItem;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionControlledVocabularyItemFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


public class CollectionControlledVocabularyItemServiceIT extends CollectionModuleBaseIT {

  private static final String GROUP = "grp";

  @Test
  void onUpdate_lastUpdatedOnUpdated() {
    CollectionControlledVocabularyItem attribute = newAttribute(CollectionVocabularyConfiguration.DinaComponent.COLLECTING_EVENT);
    collectionControlledVocabularyItemService.create(attribute);
    collectionControlledVocabularyItemService.update(attribute);

    collectionControlledVocabularyItemService.detach(attribute);
    assertNotNull(collectionControlledVocabularyItemService.findOne(attribute.getUuid(),
      CollectionControlledVocabularyItem.class).getLastUpdatedOn());
  }

  @Test
  void delete_WhenNotInUse_DeleteAccepted() {
    CollectionControlledVocabularyItem attribute = newAttribute(CollectionVocabularyConfiguration.DinaComponent.COLLECTING_EVENT);
    collectionControlledVocabularyItemService.create(attribute);

    assertNotNull(
      collectionControlledVocabularyItemService.findOne(attribute.getUuid(), CollectionControlledVocabularyItem.class));
    collectionControlledVocabularyItemService.delete(attribute);

    assertNull(
      collectionManagedAttributeService.findOne(attribute.getUuid(), CollectionControlledVocabularyItem.class));
  }

  @Test
  void delete_WhenKeyInUseByEvent_DeniesDelete() {
    CollectionControlledVocabularyItem attribute = newAttribute(CollectionVocabularyConfiguration.DinaComponent.COLLECTING_EVENT);
    collectionControlledVocabularyItemService.create(attribute);

    CollectingEvent event = newEvent();
    event.setManagedAttributes(new HashMap<>(Map.of(attribute.getKey(), "test value")));
    collectingEventService.create(event);
    Assertions.assertTrue(
      collectingEventService.findOne(event.getUuid(), CollectingEvent.class).getManagedAttributes()
        .containsKey(attribute.getKey()));

    Assertions.assertThrows(
      IllegalStateException.class, () -> collectionControlledVocabularyItemService.delete(attribute));
  }

  @Test
  void delete_WhenKeyInUseBySample_DeniesDelete() {
    CollectionControlledVocabularyItem attribute = newAttribute(CollectionVocabularyConfiguration.DinaComponent.MATERIAL_SAMPLE);
    collectionControlledVocabularyItemService.create(attribute);

    MaterialSample sample = MaterialSample.builder().group(GROUP).createdBy("by").build();
    sample.setManagedAttributes(new HashMap<>(Map.of(attribute.getKey(), "test value")));
    materialSampleService.create(sample);

    Assertions.assertTrue(
      collectingEventService.findOne(sample.getUuid(), MaterialSample.class).getManagedAttributes()
        .containsKey(attribute.getKey()));

    Assertions.assertThrows(
      IllegalStateException.class, () -> collectionControlledVocabularyItemService.delete(attribute));
  }

  @Test
  void delete_WhenManagedAttributeComponentIsDetermination_DeleteAccepted() {
    CollectionControlledVocabularyItem attribute = newAttribute(CollectionVocabularyConfiguration.DinaComponent.DETERMINATION);
    collectionControlledVocabularyItemService.create(attribute);

    assertNotNull(
      collectionControlledVocabularyItemService.findOne(attribute.getUuid(), CollectionControlledVocabularyItem.class));

    collectionControlledVocabularyItemService.delete(attribute);

    assertNull(
      collectionControlledVocabularyItemService.findOne(attribute.getUuid(), CollectionControlledVocabularyItem.class));
  }

  private CollectionControlledVocabulary getManagedAttributeControlledVocabularyRef() {
    return collectionControlledVocabularyService.getReferenceByNaturalId(
      CollectionControlledVocabulary.class,
      CollectionVocabularyConfiguration.MANAGED_ATTRIBUTE_VOCAB_UUID
    );
  }

  private CollectionControlledVocabularyItem newAttribute(CollectionVocabularyConfiguration.DinaComponent component) {
    return CollectionControlledVocabularyItemFactory
      .newCollectionManagedAttribute()
      .controlledVocabulary(getManagedAttributeControlledVocabularyRef())
      .createdBy("CollectionManagedAttributeServiceIT")
      .dinaComponent(component.name())
      .group(GROUP)
      .acceptedValues(null)
      .build();
  }

  private static CollectingEvent newEvent() {
    return CollectingEvent.builder()
      .createdBy("CollectionManagedAttributeServiceIT")
      .group(GROUP)
      .startEventDateTime(LocalDateTime.now().minusDays(1))
      .build();
  }

}
