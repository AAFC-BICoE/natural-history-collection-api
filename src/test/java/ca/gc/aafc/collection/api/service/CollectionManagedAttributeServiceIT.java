package ca.gc.aafc.collection.api.service;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.entity.ManagedAttribute;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class CollectionManagedAttributeServiceIT extends CollectionModuleBaseIT {

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
      IllegalArgumentException.class, () -> collectionManagedAttributeService.delete(attribute));
  }

  @Test
  void delete_WhenKeyInUseBySample_DeniesDelete() {
    CollectionManagedAttribute attribute = newAttribute(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE);
    collectionManagedAttributeService.create(attribute);

    MaterialSample sample = MaterialSample.builder().group("grp").createdBy("by").build();
    sample.setManagedAttributes(new HashMap<>(Map.of(attribute.getKey(), "test value")));
    materialSampleService.create(sample);

    Assertions.assertTrue(
      collectingEventService.findOne(sample.getUuid(), MaterialSample.class).getManagedAttributes()
        .containsKey(attribute.getKey()));

    Assertions.assertThrows(
      IllegalArgumentException.class, () -> collectionManagedAttributeService.delete(attribute));
  }

  private static CollectionManagedAttribute newAttribute(CollectionManagedAttribute.ManagedAttributeComponent component) {
    return CollectionManagedAttribute.builder()
      .group("grp")
      .name(RandomStringUtils.randomAlphabetic(6))
      .managedAttributeType(ManagedAttribute.ManagedAttributeType.STRING)
      .createdBy("CollectionManagedAttributeServiceIT")
      .managedAttributeComponent(component)
      .build();
  }

  private static CollectingEvent newEvent() {
    return CollectingEvent.builder()
      .createdBy("CollectionManagedAttributeServiceIT")
      .group("grp")
      .startEventDateTime(LocalDateTime.now().minusDays(1))
      .build();
  }

}
