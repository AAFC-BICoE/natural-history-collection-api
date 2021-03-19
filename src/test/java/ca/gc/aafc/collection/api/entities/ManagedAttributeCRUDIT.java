package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.ManagedAttributeService;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ManagedAttributeCRUDIT extends CollectionModuleBaseIT {
  @Inject
  private BaseDAO baseDAO;
  private DefaultDinaService<ManagedAttribute> maService;

  @BeforeEach
  void setUp() {
    maService = new ManagedAttributeService(baseDAO);
  }

  @Test
  void create_recordCreated() {
    String expectedValue = "value";
    String expectedCreatedBy = "dina";
    String expectedName = "dina test attribute";
    UUID uuid = maService.create(ManagedAttribute.builder()
      .uuid(UUID.randomUUID())
      .managedAttributeType(ManagedAttribute.ManagedAttributeType.STRING)
      .acceptedValues(new String[]{expectedValue})
      .managedAttributeComponent(ManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT)
      .createdBy(expectedCreatedBy)
      .name(expectedName)
      .build()).getUuid();

    ManagedAttribute result = maService.findOne(uuid, ManagedAttribute.class);
    assertNotNull(result.getId());
    assertNotNull(result.getCreatedOn());
    assertEquals(expectedCreatedBy, result.getCreatedBy());
    assertEquals(expectedName, result.getName());
    assertEquals(expectedValue, result.getAcceptedValues()[0]);
    assertEquals(
      ManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT,
      result.getManagedAttributeComponent());
    assertEquals(
      ManagedAttribute.ManagedAttributeType.STRING,
      result.getManagedAttributeType());


    result.setKey("abc");
    result.setName("new name");

    maService.update(result);

    //detach the object to force a reload from the database
    service.detach(result);

    result = maService.findOne(uuid, ManagedAttribute.class);
    assertNotEquals("abc", result.getKey());
    assertNotEquals("new name", result.getName());
  }

}
