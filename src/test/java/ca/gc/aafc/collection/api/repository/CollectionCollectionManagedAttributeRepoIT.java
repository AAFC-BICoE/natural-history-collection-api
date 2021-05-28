package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.UUID;

public class CollectionCollectionManagedAttributeRepoIT extends CollectionModuleBaseIT {

  @Inject
  private CollectionManagedAttributeRepo repo;

  @Test
  void create_recordCreated() {
    String expectedName = "dina attribute #12";
    String expectedValue = "dina value";
    String expectedCreatedBy = "dina";
    String expectedGroup = "dinaGroup";

    CollectionManagedAttributeDto dto = new CollectionManagedAttributeDto();
    dto.setName(expectedName);
    dto.setManagedAttributeType(CollectionManagedAttribute.ManagedAttributeType.INTEGER);
    dto.setAcceptedValues(new String[]{expectedValue});
    dto.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    dto.setCreatedBy(expectedCreatedBy);
    dto.setGroup(expectedGroup);

    UUID uuid = repo.create(dto).getUuid();
    CollectionManagedAttributeDto result = repo.findOne(uuid, new QuerySpec(
        CollectionManagedAttributeDto.class));
    Assertions.assertEquals(uuid, result.getUuid());
    Assertions.assertEquals(expectedName, result.getName());
    Assertions.assertEquals("dina_attribute_12", result.getKey());
    Assertions.assertEquals(expectedValue, result.getAcceptedValues()[0]);
    Assertions.assertEquals(expectedCreatedBy, result.getCreatedBy());
    Assertions.assertEquals(expectedGroup, result.getGroup());
    Assertions.assertEquals(CollectionManagedAttribute.ManagedAttributeType.INTEGER, result.getManagedAttributeType());
    Assertions.assertEquals(
      CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT,
      result.getManagedAttributeComponent());
  }

  @Test
  void findOneByKey_whenKeyProvided_managedAttributeFetched() {
    CollectionManagedAttributeDto newAttribute = new CollectionManagedAttributeDto();
    newAttribute.setName("Collecting Event Attribute 1");
    newAttribute.setManagedAttributeType(CollectionManagedAttribute.ManagedAttributeType.INTEGER);
    newAttribute.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    newAttribute.setCreatedBy("poffm");
    newAttribute.setGroup("group");

    UUID newAttributeUuid = repo.create(newAttribute).getUuid();

    QuerySpec querySpec = new QuerySpec(CollectionManagedAttributeDto.class);
    CollectionManagedAttributeDto fetchedAttribute = repo.findOne("collecting_event.collecting_event_attribute_1", querySpec);

    Assertions.assertEquals(newAttributeUuid, fetchedAttribute.getUuid());
  }

}
