package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectorGroupDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectorGroup;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.CollectorGroupFactory;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CollectingEventRepositoryIT extends CollectionModuleBaseIT {
  @Inject
  private CollectingEventRepository collectingEventRepository;

  private CollectingEvent testCollectingEvent;
  private CollectorGroup groupRelation;

  private static final LocalDate startDate = LocalDate.of(2000, 1, 1);
  private static final LocalTime startTime = LocalTime.of(0, 1);

  private static final LocalDate endDate = LocalDate.of(2002, 10, 10);
  private static final LocalTime endTime = LocalTime.of(10, 10);

  @BeforeEach
  public void setup() {
    groupRelation = createCollectorGroup();
    testCollectingEvent = createTestCollectingEvent(groupRelation);
  }

  private CollectingEvent createTestCollectingEvent(CollectorGroup groupRelation) {
    CollectingEvent newEvent = CollectingEventFactory.newCollectingEvent()
      .startEventDateTime(LocalDateTime.of(startDate, startTime))
      .startEventDateTimePrecision((byte) 8)
      .endEventDateTime(LocalDateTime.of(endDate, endTime))
      .verbatimCollectors("Jack and Jane")
      .endEventDateTimePrecision((byte) 8)
      .verbatimEventDateTime("XI-02-1798")
      .decimalLatitude(26.089)
      .decimalLongitude(106.36)
      .coordinateUncertaintyInMeters(208)
      .verbatimCoordinates("26.089, 106.36")
      .attachment(List.of(UUID.randomUUID()))
      .collectors(List.of(UUID.randomUUID()))
      .collectorGroupUuid(UUID.randomUUID())
      .collectorGroup(groupRelation)
      .build();

    service.save(newEvent);
    return newEvent;
  }

  private CollectorGroup createCollectorGroup() {
    CollectorGroup group = CollectorGroupFactory.newCollectorGroup().build();
    service.save(group);
    return group;
  }

  @Test
  public void findCollectingEvent_whenNoFieldsAreSelected_CollectingEventReturnedWithAllFields() {
    CollectingEventDto collectingEventDto = collectingEventRepository
      .findOne(testCollectingEvent.getUuid(), new QuerySpec(CollectingEventDto.class));
    assertNotNull(collectingEventDto);
    assertEquals(testCollectingEvent.getUuid(), collectingEventDto.getUuid());
    assertEquals(testCollectingEvent.getCreatedBy(), collectingEventDto.getCreatedBy());
    assertEquals(
      testCollectingEvent.supplyStartISOEventDateTime().toString(),
      collectingEventDto.getStartEventDateTime());
    assertEquals(
      testCollectingEvent.supplyEndISOEventDateTime().toString(),
      collectingEventDto.getEndEventDateTime());
    assertEquals("XI-02-1798", collectingEventDto.getVerbatimEventDateTime());
    assertEquals(26.089, collectingEventDto.getDecimalLatitude());
    assertEquals(106.36, collectingEventDto.getDecimalLongitude());
    assertEquals(208, collectingEventDto.getCoordinateUncertaintyInMeters());
    assertEquals("26.089, 106.36", collectingEventDto.getVerbatimCoordinates());
    assertEquals(
      testCollectingEvent.getAttachment().get(0).toString(),
      collectingEventDto.getAttachment().get(0).getId());
    assertEquals(
      testCollectingEvent.getCollectors().get(0).toString(),
      collectingEventDto.getCollectors().get(0).getId());
    assertEquals("Jack and Jane", testCollectingEvent.getVerbatimCollectors());
    assertEquals(testCollectingEvent.getCollectorGroupUuid(), collectingEventDto.getCollectorGroupUuid());
    assertEquals(groupRelation.getUuid(), testCollectingEvent.getCollectorGroup().getUuid());
  }

  @Test
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    CollectorGroupDto groupDto = new CollectorGroupDto();
    groupDto.setUuid(groupRelation.getUuid());
    CollectingEventDto ce = new CollectingEventDto();
    ce.setUuid(UUID.randomUUID());
    ce.setCollectorGroupUuid(UUID.randomUUID());
    ce.setVerbatimCollectors("Jack and Jane");
    ce.setStartEventDateTime(ISODateTime.parse("2007-12-03T10:15:30").toString());
    ce.setEndEventDateTime(ISODateTime.parse("2007-12-04T11:20:20").toString());
    ce.setVerbatimCoordinates("26.089, 106.36");
    ce.setCollectorGroup(groupDto);
    ce.setAttachment(List.of(
      ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("file").build()));
    ce.setCollectors(
      List.of(ExternalRelationDto.builder().type("agent").id(UUID.randomUUID().toString()).build()));
    CollectingEventDto result = collectingEventRepository.findOne(
      collectingEventRepository.create(ce).getUuid(),
      new QuerySpec(CollectingEventDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(ce.getAttachment().get(0).getId(), result.getAttachment().get(0).getId());
    assertEquals(ce.getCollectors().get(0).getId(), result.getCollectors().get(0).getId());
    assertEquals("Jack and Jane", result.getVerbatimCollectors());
    assertEquals(ce.getCollectorGroupUuid(), result.getCollectorGroupUuid());
    assertEquals(groupRelation.getUuid(), result.getCollectorGroup().getUuid());
  }

}
