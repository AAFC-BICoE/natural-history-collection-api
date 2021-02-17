package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.ManagedAttributeDto;
import ca.gc.aafc.collection.api.repository.CollectingEventRepository;
import ca.gc.aafc.collection.api.repository.ManagedAttributeRepo;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.inject.Inject;

class CollectingEventManagedAttributeTest extends CollectionModuleBaseIT {
  @Inject
  private ManagedAttributeRepo managedAttributeRepo;

  @Inject
  private CollectingEventRepository eventRepository;

  @Test
  void name() {

    ManagedAttributeDto attribute = managedAttributeRepo.findOne(
      managedAttributeRepo.create(newAttributeDto()).getUuid(), new QuerySpec(ManagedAttributeDto.class));

    CollectingEventDto event = eventRepository.findOne(
      eventRepository.create(newEventDto()).getUuid(), new QuerySpec(CollectingEventDto.class));

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