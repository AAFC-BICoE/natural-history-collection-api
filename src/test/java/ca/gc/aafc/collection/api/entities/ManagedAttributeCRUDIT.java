package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

class ManagedAttributeCRUDIT extends CollectionModuleBaseIT {
  @Inject
  private BaseDAO baseDAO;
  private DefaultDinaService<ManagedAttribute> service;

  @BeforeEach
  void setUp() {
    service = new DefaultDinaService<>(baseDAO);
  }

  @Test
  void create_recordCreated() {
    String expectedValue = "value";
    String expectedCreatedBy = "dina";
    String expectedName = "dina test attribute";
    UUID uuid = service.create(ManagedAttribute.builder()
      .uuid(UUID.randomUUID())
      .managedAttributeType(ManagedAttribute.ManagedAttributeType.STRING)
      .acceptedValues(new String[]{expectedValue})
      .managedAttributeComponent(ManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT)
      .createdBy(expectedCreatedBy)
      .name(expectedName)
      .build()).getUuid();

    ManagedAttribute result = service.findOne(uuid, ManagedAttribute.class);
    Assertions.assertNotNull(result.getId());
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(expectedCreatedBy, result.getCreatedBy());
    Assertions.assertEquals(expectedName, result.getName());
    Assertions.assertEquals(expectedValue, result.getAcceptedValues()[0]);
    Assertions.assertEquals(
      ManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT,
      result.getManagedAttributeComponent());
    Assertions.assertEquals(
      ManagedAttribute.ManagedAttributeType.STRING,
      result.getManagedAttributeType());
  }

}
