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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
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

  private static final String dwcRecordedBy = "Julian Grant | Noah Hart";
  private static final String dwcVerbatimLocality = "25 km NNE Bariloche por R. Nac. 237";
  private static final String dwcGeoreferenceSources = "https://www.geonames.org/";
  private static final OffsetDateTime dwcGeoreferencedDate = OffsetDateTime.now();

  @BeforeEach
  public void setup() {
    createTestCollectingEvent();
  }

  @AfterEach
  void tearDown() {
    collectingEventRepository.findAll(new QuerySpec(CollectingEventDto.class))
      .forEach(collectingEventDto -> collectingEventRepository.delete(collectingEventDto.getUuid()));
  }

  private void createTestCollectingEvent() {
    testCollectingEvent = CollectingEventFactory.newCollectingEvent()
      .startEventDateTime(LocalDateTime.of(startDate, startTime))
      .startEventDateTimePrecision((byte) 8)
      .endEventDateTime(LocalDateTime.of(endDate, endTime))
      .endEventDateTimePrecision((byte) 8)
      .verbatimEventDateTime("XI-02-1798")
      .dwcDecimalLatitude(26.089)
      .dwcDecimalLongitude(106.36)
      .dwcCoordinateUncertaintyInMeters(208)
      .dwcVerbatimCoordinates("26.089, 106.36")
      .attachment(List.of(UUID.randomUUID()))
      .collectors(List.of(UUID.randomUUID()))
      .dwcRecordedBy(dwcRecordedBy)
      .dwcVerbatimLocality(dwcVerbatimLocality)
      .dwcGeoreferencedDate(dwcGeoreferencedDate)
      .dwcGeoreferenceSources(dwcGeoreferenceSources)
      .dwcGeoreferencedBy(List.of(UUID.randomUUID()))
      .build();

    service.save(testCollectingEvent);
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
    assertEquals(26.089, collectingEventDto.getDwcDecimalLatitude());
    assertEquals(106.36, collectingEventDto.getDwcDecimalLongitude());
    assertEquals(208, collectingEventDto.getDwcCoordinateUncertaintyInMeters());
    assertEquals("26.089, 106.36", collectingEventDto.getDwcVerbatimCoordinates());
    assertEquals(dwcRecordedBy, collectingEventDto.getDwcRecordedBy());
    assertEquals(
      testCollectingEvent.getAttachment().get(0).toString(),
      collectingEventDto.getAttachment().get(0).getId());
    assertEquals(
      testCollectingEvent.getCollectors().get(0).toString(),
      collectingEventDto.getCollectors().get(0).getId());
    assertEquals(dwcVerbatimLocality, collectingEventDto.getDwcVerbatimLocality());
    assertEquals(dwcGeoreferenceSources, collectingEventDto.getDwcGeoreferenceSources());
    assertEquals(dwcGeoreferencedDate, collectingEventDto.getDwcGeoreferencedDate());
    assertEquals(
      testCollectingEvent.getDwcGeoreferencedBy().get(0).toString(),
      collectingEventDto.getDwcGeoreferencedBy().get(0).getId());
  }

  @ParameterizedTest
  @MethodSource({"precisionFilterSource"})
  void findAll_PrecisionBoundsTest_DateFilteredCorrectly(String startDate, String input, int expectedSize) {
    collectingEventRepository.create(newEventDto(startDate, "1888"));
    assertEquals(expectedSize, collectingEventRepository.findAll(newRsqlQuerySpec(input)).size());
  }

  private static Stream<Arguments> precisionFilterSource() {
    return Stream.of(
      Arguments.of("2000", "startEventDateTime==2000-03", 0),
      Arguments.of("2000-03", "startEventDateTime==2000-03", 1),
      Arguments.of("2000-03-03", "startEventDateTime==2000-03", 1)
    );
  }

  @Test
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    CollectingEventDto ce = newEventDto("2007-12-03T10:15:30", "2007-12-04T11:20:20");
    CollectingEventDto result = collectingEventRepository.findOne(
      collectingEventRepository.create(ce).getUuid(),
      new QuerySpec(CollectingEventDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(ce.getAttachment().get(0).getId(), result.getAttachment().get(0).getId());
    assertEquals(ce.getCollectors().get(0).getId(), result.getCollectors().get(0).getId());
    assertEquals(dwcRecordedBy, result.getDwcRecordedBy());
    assertEquals(dwcVerbatimLocality, result.getDwcVerbatimLocality());
    assertEquals(dwcGeoreferenceSources, result.getDwcGeoreferenceSources());
    assertEquals(dwcGeoreferencedDate, result.getDwcGeoreferencedDate());
    assertEquals(ce.getDwcGeoreferencedBy().get(0).getId(), result.getDwcGeoreferencedBy().get(0).getId());
  }

  private CollectingEventDto newEventDto(String startTime, String endDate) {
    CollectingEventDto ce = new CollectingEventDto();
    ce.setUuid(UUID.randomUUID());
    ce.setGroup("test group");
    ce.setStartEventDateTime(ISODateTime.parse(startTime).toString());
    ce.setEndEventDateTime(ISODateTime.parse(endDate).toString());
    ce.setDwcVerbatimCoordinates("26.089, 106.36");
    ce.setDwcRecordedBy(dwcRecordedBy);
    ce.setAttachment(List.of(
      ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("file").build()));
    ce.setCollectors(
      List.of(ExternalRelationDto.builder().type("agent").id(UUID.randomUUID().toString()).build()));
    ce.setDwcVerbatimLocality(dwcVerbatimLocality);
    ce.setDwcGeoreferencedDate(dwcGeoreferencedDate);
    ce.setDwcGeoreferenceSources(dwcGeoreferenceSources);
    ce.setDwcGeoreferencedBy(List.of(ExternalRelationDto.builder()
      .type("agent")
      .id(UUID.randomUUID().toString())
      .build()));
    return ce;
  }

  private static QuerySpec newRsqlQuerySpec(String rsql) {
    QuerySpec spec = new QuerySpec(CollectingEventDto.class);
    spec.addFilter(PathSpec.of("rsql").filter(FilterOperator.EQ, rsql));
    return spec;
  }

}
