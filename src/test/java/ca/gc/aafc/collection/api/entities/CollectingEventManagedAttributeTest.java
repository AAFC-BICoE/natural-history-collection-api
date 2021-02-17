package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectingEventManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.ManagedAttributeDto;
import ca.gc.aafc.collection.api.repository.CollectingEventRepository;
import ca.gc.aafc.collection.api.repository.ManagedAttributeRepo;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;
import java.util.List;

class CollectingEventManagedAttributeTest extends CollectionModuleBaseIT {
  @Inject
  private ManagedAttributeRepo managedAttributeRepo;

  @Inject
  private CollectingEventRepository eventRepository;

  @Test
  void name() {
    ManagedAttributeDto attribute = managedAttributeRepo.findOne(
      managedAttributeRepo.create(newAttributeDto()).getUuid(), new QuerySpec(ManagedAttributeDto.class));

    CollectingEventDto event = newEventDto();
    String expectedValue = "99";
    event.setManagedAttributes(List.of(CollectingEventManagedAttributeDto.builder()
      .assignedValue(expectedValue)
      .attributeId(attribute.getUuid())
      .build()));

    CollectingEventDto result = eventRepository.findOne(
      eventRepository.create(event).getUuid(), new QuerySpec(CollectingEventDto.class));

    Assertions.assertEquals(expectedValue, result.getManagedAttributes().get(0).getAssignedValue());
    Assertions.assertEquals(attribute.getName(), result.getManagedAttributes().get(0).getName());
    Assertions.assertEquals(attribute.getUuid(), result.getManagedAttributes().get(0).getAttributeId());
  }

  private ManagedAttributeDto newAttributeDto() {
    ManagedAttributeDto dto = new ManagedAttributeDto();
    dto.setName(RandomStringUtils.randomAlphabetic(5));
    dto.setManagedAttributeType(ManagedAttribute.ManagedAttributeType.INTEGER);
    dto.setAcceptedValues(new String[]{RandomStringUtils.randomAlphabetic(5)});
    dto.setManagedAttributeComponent(ManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    dto.setCreatedBy(RandomStringUtils.randomAlphabetic(5));
    return dto;
  }

  private CollectingEventDto newEventDto() {
    CollectingEventDto ce = new CollectingEventDto();
    ce.setGroup("aafc");
    ce.setStartEventDateTime(ISODateTime.parse("2020").toString());
    ce.setCreatedBy("dina");
    return ce;
  }
}