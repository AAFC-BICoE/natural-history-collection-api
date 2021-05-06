package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.CollectionManagedAttributeService;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.web.HttpRequestMethodNotSupportedException;

class CollectionManagedAttributeCRUDIT extends CollectionModuleBaseIT {
  @Inject
  private BaseDAO baseDAO;
  private DefaultDinaService<CollectionManagedAttribute> maService;

  @BeforeEach
  void setUp() {
    maService = new CollectionManagedAttributeService(baseDAO);
  }

  @Test
  void create_recordCreated() {
    String expectedValue = "value";
    String expectedCreatedBy = "dina";
    String expectedName = "dina test attribute";
    UUID uuid = maService.create(CollectionManagedAttribute.builder()
      .uuid(UUID.randomUUID())
      .managedAttributeType(CollectionManagedAttribute.ManagedAttributeType.STRING)
      .acceptedValues(new String[]{expectedValue})
      .managedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT)
      .createdBy(expectedCreatedBy)
      .name(expectedName)
      .build()).getUuid();

    CollectionManagedAttribute result = maService.findOne(uuid, CollectionManagedAttribute.class);
    assertNotNull(result.getId());
    assertNotNull(result.getCreatedOn());
    assertEquals(expectedCreatedBy, result.getCreatedBy());
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

    maService.update(result);

    //detach the object to force a reload from the database
    service.detach(result);

    result = maService.findOne(uuid, CollectionManagedAttribute.class);
    assertNotEquals("abc", result.getKey());
    assertNotEquals("new name", result.getName());

    // delete is disabled for now
    CollectionManagedAttribute finalResult = result;
    assertThrows(HttpRequestMethodNotSupportedException.class, () -> maService.delete(finalResult));
  }

}
