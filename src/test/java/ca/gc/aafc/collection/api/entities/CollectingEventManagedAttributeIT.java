package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectingEventManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.ManagedAttributeDto;
import ca.gc.aafc.collection.api.repository.CollectingEventAttributeRepository;
import ca.gc.aafc.collection.api.repository.CollectingEventRepository;
import ca.gc.aafc.collection.api.repository.ManagedAttributeRepo;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;

class CollectingEventManagedAttributeIT extends CollectionModuleBaseIT {
  @Inject
  private ManagedAttributeRepo managedAttributeRepo;

  @Inject
  private CollectingEventRepository eventRepository;

  @Inject
  private CollectingEventAttributeRepository repo;

  @Test
  void create_recordCreated() {
    String expectedValue = "99";
    ManagedAttributeDto attribute = managedAttributeRepo.findOne(
      managedAttributeRepo.create(newManagedAttribute()).getUuid(), new QuerySpec(ManagedAttributeDto.class));
    CollectingEventDto event = eventRepository.findOne(
      eventRepository.create(newEventDto()).getUuid(), new QuerySpec(CollectingEventDto.class));

    CollectingEventManagedAttributeDto result = repo.findOne(
      repo.create(newEventAttribute(expectedValue, attribute, event)).getUuid(),
      new QuerySpec(CollectingEventManagedAttributeDto.class));

    Assertions.assertEquals(expectedValue, result.getAssignedValue());
    Assertions.assertEquals(attribute.getUuid(), result.getAttribute().getUuid());
    Assertions.assertEquals(event.getUuid(), result.getEvent().getUuid());
  }

  private CollectingEventManagedAttributeDto newEventAttribute(
    String expectedValue,
    ManagedAttributeDto attribute,
    CollectingEventDto event
  ) {
    return CollectingEventManagedAttributeDto.builder()
      .assignedValue(expectedValue)
      .attribute(attribute)
      .event(event)
      .build();
  }

  private ManagedAttributeDto newManagedAttribute() {
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