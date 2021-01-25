package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CollectingEventRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private CollectingEventRepository collectingEventRepository;

  private CollectingEvent testCollectingEvent;

  private static final LocalDate startDate = LocalDate.of(2000, 1, 1);
  private static final LocalTime startTime = LocalTime.of(0, 1);

  private static final LocalDate endDate = LocalDate.of(2002, 10, 10);
  private static final LocalTime endTime = LocalTime.of(10, 10);

  @BeforeEach
  public void setup() {
    createTestCollectingEvent();
  }

  private CollectingEvent createTestCollectingEvent() {
    testCollectingEvent = CollectingEventFactory.newCollectingEvent()
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
      .build();

    service.save(testCollectingEvent);
    return testCollectingEvent;
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
  }

  @ParameterizedTest
  @MethodSource({"startDateFilterTestSource", "endDateFilterTestSource"})
  void findAll_WhenDateFiltered_DateFiltered(String input, int expectedSize) {
    Assertions.assertEquals(expectedSize, collectingEventRepository.findAll(newRsqlQuerySpec(input)).size());

  }

  private static Stream<Arguments> startDateFilterTestSource() {
    return Stream.of(
      Arguments.of("startEventDateTime=ge=1999", 1),
      Arguments.of("startEventDateTime=ge=2020", 0),
      Arguments.of("startEventDateTime=le=2020", 1),
      Arguments.of("startEventDateTime=le=1999", 0),
      Arguments.of("startEventDateTime=le=2001 and startEventDateTime=ge=1999", 1),
      Arguments.of("startEventDateTime=le=1999 and startEventDateTime=ge=2001", 0),
      Arguments.of("startEventDateTime=le=1999 or startEventDateTime=ge=1999", 1),
      Arguments.of("startEventDateTime=le=1999 or startEventDateTime=ge=2200", 0)
    );
  }

  private static Stream<Arguments> endDateFilterTestSource() {
    return Stream.of(
      Arguments.of("endEventDateTime=ge=2001", 1),
      Arguments.of("endEventDateTime=ge=2003", 0),
      Arguments.of("endEventDateTime=le=2003", 1),
      Arguments.of("endEventDateTime=le=2001", 0),
      Arguments.of("endEventDateTime=le=2003 and endEventDateTime=ge=2001", 1),
      Arguments.of("endEventDateTime=le=2001 and endEventDateTime=ge=2003", 0),
      Arguments.of("endEventDateTime=le=2003 or endEventDateTime=ge=2003", 1),
      Arguments.of("endEventDateTime=le=2001 or endEventDateTime=ge=2200", 0)
    );
  }

  @Test
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    CollectingEventDto ce = new CollectingEventDto();
    ce.setUuid(UUID.randomUUID());
    ce.setGroup("test group");
    ce.setCollectorGroupUuid(UUID.randomUUID());
    ce.setVerbatimCollectors("Jack and Jane");
    ce.setStartEventDateTime(ISODateTime.parse("2007-12-03T10:15:30").toString());
    ce.setEndEventDateTime(ISODateTime.parse("2007-12-04T11:20:20").toString());
    ce.setVerbatimCoordinates("26.089, 106.36");
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
  }

  private static QuerySpec newRsqlQuerySpec(String rsql) {
    QuerySpec spec = new QuerySpec(CollectingEventDto.class);
    spec.addFilter(PathSpec.of("rsql").filter(FilterOperator.EQ, rsql));
    return spec;
  }

}
