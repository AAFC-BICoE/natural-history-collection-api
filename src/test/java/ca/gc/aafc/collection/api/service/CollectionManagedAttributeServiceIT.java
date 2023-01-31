package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionManagedAttributeFactory;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CollectionManagedAttributeServiceIT extends CollectionModuleBaseIT {

  private static final String GROUP = "grp";

  @Test
  void delete_WhenNotInUse_DeleteAccepted() {
    CollectionManagedAttribute attribute = newAttribute(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    collectionManagedAttributeService.create(attribute);

    Assertions.assertNotNull(
      collectionManagedAttributeService.findOne(attribute.getUuid(), CollectionManagedAttribute.class));

    collectionManagedAttributeService.delete(attribute);

    Assertions.assertNull(
      collectionManagedAttributeService.findOne(attribute.getUuid(), CollectionManagedAttribute.class));
  }

  @Test
  void delete_WhenKeyInUseByEvent_DeniesDelete() {
    CollectionManagedAttribute attribute = newAttribute(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    collectionManagedAttributeService.create(attribute);

    CollectingEvent event = newEvent();
    event.setManagedAttributes(new HashMap<>(Map.of(attribute.getKey(), "test value")));
    collectingEventService.create(event);
    Assertions.assertTrue(
      collectingEventService.findOne(event.getUuid(), CollectingEvent.class).getManagedAttributes()
        .containsKey(attribute.getKey()));

    Assertions.assertThrows(
      IllegalStateException.class, () -> collectionManagedAttributeService.delete(attribute));
  }

  @Test
  void delete_WhenKeyInUseBySample_DeniesDelete() {
    CollectionManagedAttribute attribute = newAttribute(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE);
    collectionManagedAttributeService.create(attribute);

    MaterialSample sample = MaterialSample.builder().group(GROUP).createdBy("by").build();
    sample.setManagedAttributes(new HashMap<>(Map.of(attribute.getKey(), "test value")));
    materialSampleService.create(sample);

    Assertions.assertTrue(
      collectingEventService.findOne(sample.getUuid(), MaterialSample.class).getManagedAttributes()
        .containsKey(attribute.getKey()));

    Assertions.assertThrows(
      IllegalStateException.class, () -> collectionManagedAttributeService.delete(attribute));
  }

  @Test
  void delete_WhenManagedAttributeComponentIsDetermination_DeleteAccepted() {
    CollectionManagedAttribute attribute = newAttribute(CollectionManagedAttribute.ManagedAttributeComponent.DETERMINATION);
    collectionManagedAttributeService.create(attribute);

    Assertions.assertNotNull(
      collectionManagedAttributeService.findOne(attribute.getUuid(), CollectionManagedAttribute.class));

    collectionManagedAttributeService.delete(attribute);

    Assertions.assertNull(
      collectionManagedAttributeService.findOne(attribute.getUuid(), CollectionManagedAttribute.class));
  }

  private static CollectionManagedAttribute newAttribute(CollectionManagedAttribute.ManagedAttributeComponent component) {
    return CollectionManagedAttributeFactory.newCollectionManagedAttribute()
            .createdBy("CollectionManagedAttributeServiceIT")
            .managedAttributeComponent(component)
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
