package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionManagedAttributeFactory;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CollectionManagedAttributeCRUDIT extends CollectionModuleBaseIT {

  @Test
  void create_recordCreated() {
    String expectedValue = "value";
    String expectedCreatedBy = "dina";
    String expectedName = "dina test attribute";
    String expectedGroup = "dinaGroup";
    UUID uuid = collectionManagedAttributeService.create(CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .managedAttributeType(CollectionManagedAttribute.ManagedAttributeType.STRING)
      .acceptedValues(new String[]{expectedValue})
      .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT)
      .createdBy(expectedCreatedBy)
      .name(expectedName)
      .group(expectedGroup)
      .build()).getUuid();

    CollectionManagedAttribute result = collectionManagedAttributeService.findOne(uuid, CollectionManagedAttribute.class);
    assertNotNull(result.getId());
    assertNotNull(result.getCreatedOn());
    assertEquals(expectedCreatedBy, result.getCreatedBy());
    assertEquals(expectedGroup,result.getGroup());
    assertEquals(expectedName, result.getName());
    assertEquals(expectedValue, result.getAcceptedValues()[0]);
    assertEquals(
      CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT,
      result.getManagedAttributeComponent());
    assertEquals(
      CollectionManagedAttribute.ManagedAttributeType.STRING,
      result.getManagedAttributeType());


    result.setKey("abc");
    result.setName("new name");

    collectionManagedAttributeService.update(result);

    //detach the object to force a reload from the database
    service.detach(result);

    result = collectionManagedAttributeService.findOne(uuid, CollectionManagedAttribute.class);
    assertNotEquals("abc", result.getKey());
    assertNotEquals("new name", result.getName());

    // delete is disabled for now
    CollectionManagedAttribute finalResult = result;
    assertThrows(UnsupportedOperationException.class, () -> collectionManagedAttributeService.delete(finalResult));
  }

}
