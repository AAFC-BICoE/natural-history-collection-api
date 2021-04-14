package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.collection.api.service.CollectingEventService;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.GeoreferenceAssertionFactory;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.IncludeRelationSpec;
import io.crnk.core.queryspec.PathSpec;
import io.crnk.core.queryspec.QuerySpec;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectingEventRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private CollectingEventRepository collectingEventRepository;

  @Inject
  private CollectingEventService collectingEventService;

  @Inject
  private GeoreferenceAssertionRepository geoReferenceAssertionRepository;

  @Inject
  private DatabaseSupportService dbService;
  
  private CollectingEvent testCollectingEvent;

  private static final LocalDate startDate = LocalDate.of(2000, 1, 1);
  private static final LocalTime startTime = LocalTime.of(0, 1);

  private static final LocalDate endDate = LocalDate.of(2002, 10, 10);
  private static final LocalTime endTime = LocalTime.of(10, 10);

  private static final String dwcRecordedBy = "Julian Grant | Noah Hart";
  private static final String dwcVerbatimLocality = "25 km NNE Bariloche por R. Nac. 237";
  private static final String dwcVerbatimLatitude = "latitude 12.123456";
  private static final String dwcVerbatimLongitude = "long 45.01";
  private static final String dwcVerbatimCoordinateSystem = "decimal degrees";
  private static final String dwcVerbatimSRS = "EPSG:4326";
  private static final String dwcVerbatimElevation = "100-200 m";
  private static final String dwcVerbatimDepth = "10-20 m ";
  private static final LocalDate testGeoreferencedDate = LocalDate.now();
  private static final CollectingEvent.GeographicPlaceNameSource geographicPlaceNameSource = CollectingEvent.GeographicPlaceNameSource.OSM;
  private final GeoreferenceAssertion geoReferenceAssertion = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
    .dwcDecimalLatitude(12.123456)
    .dwcDecimalLongitude(45.01)
    .dwcGeoreferencedDate(testGeoreferencedDate)
    .uuid(UUID.randomUUID())
    .build();
  private final GeoreferenceAssertion geoReferenceAssertionA = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
    .dwcDecimalLatitude(0.0)
    .dwcDecimalLongitude(12.34)
    .dwcGeoreferencedDate(testGeoreferencedDate)
    .uuid(UUID.randomUUID())
    .build();
  private final GeoreferenceAssertion geoReferenceAssertionB = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
    .dwcDecimalLatitude(9.87654321)
    .dwcDecimalLongitude(177.77)
    .dwcGeoreferencedDate(testGeoreferencedDate)
    .uuid(UUID.randomUUID())
    .build();
  private final GeoreferenceAssertion primaryGeoReferenceAssertion = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
    .dwcDecimalLatitude(11.11)
    .dwcDecimalLongitude(22.22)
    .dwcGeoreferencedDate(testGeoreferencedDate)
    .uuid(UUID.randomUUID())
    .build();
  private static final String[] dwcOtherRecordNumbers = new String[] { "80-79", "80-80"};
  private static GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail = null;

  @BeforeEach
  @SneakyThrows
  @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
  public void setup() {
    geographicPlaceNameSourceDetail = GeographicPlaceNameSourceDetail.builder()
      .sourceID("1")
      .sourceIdType("N")
      .sourceUrl(new URL("https://github.com/orgs/AAFC-BICoE/dashboard"))
        // recordedOn should be overwritten by the server side generated value
      .recordedOn(OffsetDateTime.of(LocalDateTime.of(2000,01,01,11,10), ZoneOffset.ofHoursMinutes(1, 0)))
      .build();

    createTestCollectingEvent();
  }

  private void createTestCollectingEvent() {
    dbService.save(geoReferenceAssertion,false);
    testCollectingEvent = CollectingEventFactory.newCollectingEvent()
      .startEventDateTime(LocalDateTime.of(startDate, startTime))
      .startEventDateTimePrecision((byte) 8)
      .endEventDateTime(LocalDateTime.of(endDate, endTime))
      .endEventDateTimePrecision((byte) 8)
      .verbatimEventDateTime("XI-02-1798")
      .dwcVerbatimCoordinates("26.089, 106.36")
      .attachment(List.of(UUID.randomUUID()))
      .collectors(List.of(UUID.randomUUID()))
      .dwcRecordedBy(dwcRecordedBy)
      .dwcVerbatimLocality(dwcVerbatimLocality)
      .dwcVerbatimLatitude(dwcVerbatimLatitude)
      .dwcVerbatimLongitude(dwcVerbatimLongitude)
      .dwcVerbatimCoordinateSystem(dwcVerbatimCoordinateSystem)
      .dwcVerbatimSRS(dwcVerbatimSRS)
      .dwcVerbatimElevation(dwcVerbatimElevation)
      .dwcVerbatimDepth(dwcVerbatimDepth)   
      .dwcOtherRecordNumbers(dwcOtherRecordNumbers)
      .geographicPlaceNameSource(geographicPlaceNameSource)
      .geographicPlaceNameSourceDetail(geographicPlaceNameSourceDetail)
      .build();
    testCollectingEvent.setOtherGeoReferenceAssertions(Collections.singletonList(geoReferenceAssertion));
    testCollectingEvent.setPrimaryGeoreferenceAssertion(primaryGeoReferenceAssertion);

    collectingEventService.create(testCollectingEvent);
  }

  @Test
  public void updateCollectingEvent_unassignPrimaryGeoreferenceAssertionWithOneGeoreferenceAssertion_throwsIllegalArgumentException() {
    testCollectingEvent.setPrimaryGeoreferenceAssertion(null);
    IllegalArgumentException exception = 
    assertThrows(IllegalArgumentException.class, () -> {
      collectingEventService.update(testCollectingEvent);
    });

    String expectedMessage = "Must have a Primary Georeference Assertion if the Georeference Assertion list is not empty";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void updateCollectingEvent_setPrimaryAssertionToOneInList_throwsIllegalArgumentException() {
    List<GeoreferenceAssertion> georef = new ArrayList<>();
    georef.add(testCollectingEvent.getOtherGeoReferenceAssertions().iterator().next());
    georef.add(geoReferenceAssertionA);
    georef.add(geoReferenceAssertionB);
    testCollectingEvent.setOtherGeoReferenceAssertions(georef);
    testCollectingEvent.setPrimaryGeoreferenceAssertion(geoReferenceAssertionA);
    IllegalArgumentException exception = 
      assertThrows(IllegalArgumentException.class, () -> {
        collectingEventService.update(testCollectingEvent);
      });

    String expectedMessage = "Primary Georeference must not be in the Georeference Assertion list";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void updateCollectingEvent_changePrimaryGeoreferenceAssertion() {
    List<GeoreferenceAssertion> georef = new ArrayList<>();
    georef.add(testCollectingEvent.getOtherGeoReferenceAssertions().iterator().next());
    georef.add(primaryGeoReferenceAssertion);
    georef.add(geoReferenceAssertionB);
    testCollectingEvent.setOtherGeoReferenceAssertions(georef);
    testCollectingEvent.setPrimaryGeoreferenceAssertion(geoReferenceAssertionA);
    collectingEventService.update(testCollectingEvent);
    assertEquals(testCollectingEvent.getOtherGeoReferenceAssertions().size(), 3);
    assertEquals(testCollectingEvent.getPrimaryGeoreferenceAssertion().getDwcDecimalLatitude(), geoReferenceAssertionA.getDwcDecimalLatitude());
    assertEquals(testCollectingEvent.getPrimaryGeoreferenceAssertion().getDwcDecimalLongitude(), geoReferenceAssertionA.getDwcDecimalLongitude());
  }


  @Test
  public void findCollectingEvent_whenNoFieldsAreSelected_CollectingEventReturnedWithAllFields() {
    QuerySpec querySpec = new QuerySpec(CollectingEventDto.class);
    QuerySpec geoSpec = new QuerySpec(GeoreferenceAssertionDto.class);

    List<IncludeRelationSpec> includeRelationSpec = Stream.of("otherGeoReferenceAssertions")
        .map(Arrays::asList)
        .map(IncludeRelationSpec::new)
        .collect(Collectors.toList());

    querySpec.setIncludedRelations(includeRelationSpec);
    querySpec.setNestedSpecs(Collections.singletonList(geoSpec));

    CollectingEventDto collectingEventDto = collectingEventRepository
      .findOne(testCollectingEvent.getUuid(), querySpec);

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
    
    assertEquals(
      12.123456,
      collectingEventDto.getOtherGeoReferenceAssertions().iterator().next().getDwcDecimalLatitude());

    assertEquals(
      testGeoreferencedDate,
      collectingEventDto.getOtherGeoReferenceAssertions().iterator().next().getDwcGeoreferencedDate());

    assertEquals("26.089, 106.36", collectingEventDto.getDwcVerbatimCoordinates());
    assertEquals(dwcRecordedBy, collectingEventDto.getDwcRecordedBy());
    assertEquals(
      testCollectingEvent.getAttachment().get(0).toString(),
      collectingEventDto.getAttachment().get(0).getId());
    assertEquals(
      testCollectingEvent.getCollectors().get(0).toString(),
      collectingEventDto.getCollectors().get(0).getId());
    assertEquals(dwcVerbatimLocality, collectingEventDto.getDwcVerbatimLocality());
    assertEquals(dwcVerbatimLatitude, collectingEventDto.getDwcVerbatimLatitude());
    assertEquals(dwcVerbatimLongitude, collectingEventDto.getDwcVerbatimLongitude());
    assertEquals(dwcVerbatimCoordinateSystem, collectingEventDto.getDwcVerbatimCoordinateSystem());
    assertEquals(dwcVerbatimSRS, collectingEventDto.getDwcVerbatimSRS());
    assertEquals(dwcVerbatimElevation, collectingEventDto.getDwcVerbatimElevation());
    assertEquals(dwcVerbatimDepth, collectingEventDto.getDwcVerbatimDepth());          
    assertEquals(dwcOtherRecordNumbers[1], collectingEventDto.getDwcOtherRecordNumbers()[1]);
    assertEquals(geographicPlaceNameSource, collectingEventDto.getGeographicPlaceNameSource());
    assertEquals(
      geographicPlaceNameSourceDetail.getSourceID(),
      collectingEventDto.getGeographicPlaceNameSourceDetail().getSourceID());
    // assigned server-side
    assertNotNull(collectingEventDto.getGeographicPlaceNameSourceDetail().getRecordedOn());
    assertNotEquals(2000, collectingEventDto.getGeographicPlaceNameSourceDetail().getRecordedOn().getYear());
    assertNotNull(collectingEventDto.getGeographicPlaceNameSourceDetail().getSourceUrl());
    assertEquals(
      geographicPlaceNameSourceDetail.getSourceIdType(),
      collectingEventDto.getGeographicPlaceNameSourceDetail().getSourceIdType());
  }

  @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})   
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
    assertEquals(dwcVerbatimLatitude, result.getDwcVerbatimLatitude());
    assertEquals(dwcVerbatimLongitude, result.getDwcVerbatimLongitude());
    assertEquals(dwcVerbatimCoordinateSystem, result.getDwcVerbatimCoordinateSystem());
    assertEquals(dwcVerbatimSRS, result.getDwcVerbatimSRS());
    assertEquals(dwcVerbatimElevation, result.getDwcVerbatimElevation());
    assertEquals(dwcVerbatimDepth, result.getDwcVerbatimDepth());
    assertEquals(dwcVerbatimLatitude, result.getDwcVerbatimLatitude());
    assertEquals(dwcVerbatimLongitude, result.getDwcVerbatimLongitude());
    assertEquals(dwcVerbatimCoordinateSystem, result.getDwcVerbatimCoordinateSystem());
    assertEquals(dwcVerbatimSRS, result.getDwcVerbatimSRS());
    assertEquals(dwcVerbatimElevation, result.getDwcVerbatimElevation());
    assertEquals(dwcVerbatimDepth, result.getDwcVerbatimDepth());
    assertEquals(dwcVerbatimLatitude, result.getDwcVerbatimLatitude());
    assertEquals(dwcVerbatimLongitude, result.getDwcVerbatimLongitude());
    assertEquals(dwcVerbatimCoordinateSystem, result.getDwcVerbatimCoordinateSystem());
    assertEquals(dwcVerbatimSRS, result.getDwcVerbatimSRS());
    assertEquals(dwcVerbatimElevation, result.getDwcVerbatimElevation());
    assertEquals(dwcVerbatimDepth, result.getDwcVerbatimDepth());
    assertEquals(dwcOtherRecordNumbers[1], result.getDwcOtherRecordNumbers()[1]);         
  }

  @Test
  public void nullStartTimeNonNullEndTime_throwsIllegalArgumentException() {
      testCollectingEvent = CollectingEventFactory.newCollectingEvent()
          .endEventDateTime(LocalDateTime.of(2008, 1, 1, 1, 1, 1))
          .build();
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        collectingEventService.create(testCollectingEvent);
      });

      String expectedMessage = "The start and end dates do not create a valid timeline";
      String actualMessage = exception.getMessage();
  
      assertTrue(actualMessage.contains(expectedMessage));
    }
      

  @Test
  public void startTimeAfterEndTime_throwsIllegalArgumentException() {
      testCollectingEvent = CollectingEventFactory.newCollectingEvent()
          .startEventDateTime(LocalDateTime.of(2009, 1, 1, 1, 1, 1))
          .endEventDateTime(LocalDateTime.of(2008, 1, 1, 1, 1, 1))
          .build();
      IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        collectingEventService.create(testCollectingEvent);
      });

      String expectedMessage = "The start and end dates do not create a valid timeline";
      String actualMessage = exception.getMessage();
  
      assertTrue(actualMessage.contains(expectedMessage)); 
  }

  private CollectingEventDto newEventDto(String startDateTime, String endDateTime) {
    CollectingEventDto ce = new CollectingEventDto();
    GeoreferenceAssertionDto geoRef = new GeoreferenceAssertionDto();
    GeoreferenceAssertionDto primaryGeoRef = new GeoreferenceAssertionDto();
    geoRef.setDwcCoordinateUncertaintyInMeters(10);
    GeoreferenceAssertionDto dto = geoReferenceAssertionRepository.create(geoRef);
    GeoreferenceAssertionDto primaryDto = geoReferenceAssertionRepository.create(primaryGeoRef);
    ce.setOtherGeoReferenceAssertions(Collections.singletonList(dto));
    ce.setGroup("aafc");
    ce.setStartEventDateTime(ISODateTime.parse(startDateTime).toString());
    ce.setEndEventDateTime(ISODateTime.parse(endDateTime).toString());
    ce.setDwcVerbatimCoordinates("26.089, 106.36");
    ce.setDwcRecordedBy(dwcRecordedBy);
    ce.setAttachment(List.of(
      ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("file").build()));
    ce.setCollectors(
      List.of(ExternalRelationDto.builder().type("agent").id(UUID.randomUUID().toString()).build()));
    ce.setDwcVerbatimLocality(dwcVerbatimLocality);
    ce.setDwcVerbatimLatitude(dwcVerbatimLatitude);
    ce.setDwcVerbatimLongitude(dwcVerbatimLongitude);
    ce.setDwcVerbatimCoordinateSystem(dwcVerbatimCoordinateSystem);
    ce.setDwcVerbatimSRS(dwcVerbatimSRS);
    ce.setDwcVerbatimElevation(dwcVerbatimElevation);
    ce.setDwcVerbatimDepth(dwcVerbatimDepth);
    ce.setDwcOtherRecordNumbers(dwcOtherRecordNumbers);
    ce.setPrimaryGeoreferenceAssertion(primaryDto);

    return ce;
  }

  @ParameterizedTest
  @MethodSource({"equalFilterSource", "lt_FilterSource", "gt_FilterSource"})
  @WithMockKeycloakUser(username = "test user", groupRole = {"aafc: staff"})
  void findAll_PrecisionBoundsTest_DateFilteredCorrectly(String startDate, String input, int expectedSize) {
    collectingEventRepository.create(newEventDto(startDate, "2020"));
    assertEquals(expectedSize, collectingEventRepository.findAll(newRsqlQuerySpec(input)).size());
  }

  private static Stream<Arguments> equalFilterSource() {
    return Stream.of(
      // Format YYYY
      Arguments.of("1999", "startEventDateTime==1999", 1),
      Arguments.of("1999", "startEventDateTime==1998", 0),
      // Format YYYY-MM
      Arguments.of("1999-03", "startEventDateTime==1999-03", 1),
      Arguments.of("1999-03", "startEventDateTime==1999-02", 0),
      // Format YYYY-MM-DD
      Arguments.of("1999-03-03", "startEventDateTime==1999-03-03", 1),
      Arguments.of("1999-03-03", "startEventDateTime==1999-03-02", 0),
      // Format YYYY-MM-DD-HH-MM
      Arguments.of("1999-03-03T03:00", "startEventDateTime==1999-03-03T03:00", 1),
      Arguments.of("1999-03-03T03:00", "startEventDateTime==1999-03-03T02:00", 0),
      // Format YYYY-MM-DD-HH-MM-SS
      Arguments.of("1999-03-03T03:00:03", "startEventDateTime==1999-03-03T03:00:03", 1),
      Arguments.of("1999-03-03T03:00:03", "startEventDateTime==1999-03-03T03:00:02", 0)
    );
  }

  private static Stream<Arguments> lt_FilterSource() {
    return Stream.of(
      // Format YYYY
      Arguments.of("1999", "startEventDateTime=le=1999", 1),
      Arguments.of("1999", "startEventDateTime=le=1998", 0),

      Arguments.of("1999", "startEventDateTime=lt=1999", 0),
      Arguments.of("1999", "startEventDateTime=lt=2000", 1),

      // Format YYYY-MM
      Arguments.of("1999", "startEventDateTime=le=1999-01", 0),
      Arguments.of("1999-01", "startEventDateTime=le=1999-01", 1),
      Arguments.of("1999-01", "startEventDateTime=le=1998-12", 0),

      Arguments.of("1999-01", "startEventDateTime=lt=1999-01", 0),
      Arguments.of("1999-01", "startEventDateTime=lt=1999-02", 1),

      // Format YYYY-MM-DD
      Arguments.of("1999-01", "startEventDateTime=le=1999-01-01", 0),
      Arguments.of("1999-01-02", "startEventDateTime=le=1999-01-02", 1),
      Arguments.of("1999-01-02", "startEventDateTime=le=1999-01-01", 0),

      Arguments.of("1999-01-02", "startEventDateTime=lt=1999-01-02", 0),
      Arguments.of("1999-01-02", "startEventDateTime=lt=1999-01-03", 1),
      // Format YYYY-MM-DD-HH-MM
      Arguments.of("1999-01-02", "startEventDateTime=le=1999-01-02T02:00", 0),
      Arguments.of("1999-01-02T01:00", "startEventDateTime=le=1999-01-02T02:00", 1),
      Arguments.of("1999-01-02T02:00", "startEventDateTime=le=1999-01-02T01:00", 0),

      Arguments.of("1999-01-02T02:00", "startEventDateTime=lt=1999-01-02T02:00", 0),
      Arguments.of("1999-01-02T02:00", "startEventDateTime=lt=1999-01-02T03:00", 1)
    );
  }

  private static Stream<Arguments> gt_FilterSource() {
    return Stream.of(
      // Format YYYY
      Arguments.of("2010", "startEventDateTime=ge=2010", 1),
      Arguments.of("2010", "startEventDateTime=ge=2011", 0),

      Arguments.of("2010", "startEventDateTime=gt=2010", 0),
      Arguments.of("2010", "startEventDateTime=gt=2009", 1),

      // Format YYYY-MM
      Arguments.of("2010", "startEventDateTime=ge=2010-01", 0),
      Arguments.of("2010-01", "startEventDateTime=ge=2010-01", 1),
      Arguments.of("2010-01", "startEventDateTime=ge=2010-02", 0),

      Arguments.of("2010-01", "startEventDateTime=gt=2010-01", 0),
      Arguments.of("2010-01", "startEventDateTime=gt=2009-12", 1),

      // Format YYYY-MM-DD
      Arguments.of("2010-01", "startEventDateTime=ge=2010-01-01", 0),
      Arguments.of("2010-01-02", "startEventDateTime=ge=2010-01-02", 1),
      Arguments.of("2010-01-02", "startEventDateTime=ge=2010-01-03", 0),

      Arguments.of("2010-01-02", "startEventDateTime=gt=2010-01-02", 0),
      Arguments.of("2010-01-02", "startEventDateTime=gt=2010-01-01", 1),
      // Format YYYY-MM-DD-HH-MM
      Arguments.of("2010-01-02", "startEventDateTime=ge=2010-01-02T02:00", 0),
      Arguments.of("2010-01-02T01:00", "startEventDateTime=ge=2010-01-02T02:00", 0),
      Arguments.of("2010-01-02T02:00", "startEventDateTime=ge=2010-01-02T01:00", 1),

      Arguments.of("2010-01-02T02:00", "startEventDateTime=gt=2010-01-02T02:00", 0),
      Arguments.of("2010-01-02T02:00", "startEventDateTime=gt=2010-01-02T01:00", 1)
    );
  }


  private static QuerySpec newRsqlQuerySpec(String rsql) {
    QuerySpec spec = new QuerySpec(CollectingEventDto.class);
    spec.addFilter(PathSpec.of("rsql").filter(FilterOperator.EQ, rsql));
    return spec;
  }

}
