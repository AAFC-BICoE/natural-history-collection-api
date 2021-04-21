package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.ManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.ManagedAttribute;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

public class ManagedAttributeRepoIT extends CollectionModuleBaseIT {

  @Inject
  private ManagedAttributeRepo repo;

  @Test
  void create_recordCreated() {
    String expectedName = "dina attribute #12";
    String expectedValue = "dina value";
    String expectedCreatedBy = "dina";

    ManagedAttributeDto dto = new ManagedAttributeDto();
    dto.setName(expectedName);
    dto.setManagedAttributeType(ManagedAttribute.ManagedAttributeType.INTEGER);
    dto.setAcceptedValues(new String[]{expectedValue});
    dto.setManagedAttributeComponent(ManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    dto.setCreatedBy(expectedCreatedBy);

    UUID uuid = repo.create(dto).getUuid();
    ManagedAttributeDto result = repo.findOne(uuid, new QuerySpec(ManagedAttributeDto.class));
    Assertions.assertEquals(uuid, result.getUuid());
    Assertions.assertEquals(expectedName, result.getName());
    Assertions.assertEquals("dina_attribute_12", result.getKey());
    Assertions.assertEquals(expectedValue, result.getAcceptedValues()[0]);
    Assertions.assertEquals(expectedCreatedBy, result.getCreatedBy());
    Assertions.assertEquals(ManagedAttribute.ManagedAttributeType.INTEGER, result.getManagedAttributeType());
    Assertions.assertEquals(
      ManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT,
      result.getManagedAttributeComponent());
  }

  @Test
  void findOneByKey_whenKeyProvided_managedAttributeFetched() {
    ManagedAttributeDto newAttribute = new ManagedAttributeDto();
    newAttribute.setName("Collecting Event Attribute 1");
    newAttribute.setManagedAttributeType(ManagedAttribute.ManagedAttributeType.INTEGER);
    newAttribute.setManagedAttributeComponent(ManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    newAttribute.setCreatedBy("poffm");

    UUID newAttributeUuid = repo.create(newAttribute).getUuid();

    QuerySpec querySpec = new QuerySpec(ManagedAttributeDto.class);
    ManagedAttributeDto fetchedAttribute = repo.findOne("collecting_event.collecting_event_attribute_1", querySpec);

    Assertions.assertEquals(newAttributeUuid, fetchedAttribute.getUuid());
  }

}
