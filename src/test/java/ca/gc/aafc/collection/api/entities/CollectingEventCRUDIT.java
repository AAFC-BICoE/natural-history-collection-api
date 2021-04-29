package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.service.CollectingEventService;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.GeoreferenceAssertionFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import javax.validation.ValidationException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CollectingEventCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private DatabaseSupportService dbService;

  @Inject
  private CollectingEventService collectingEventService;

  private GeoreferenceAssertion geoReferenceAssertion;

  private static final String dwcRecordedBy = "Julian Grant | Noah Hart";
  private static final String dwcVerbatimLocality = "25 km NNE Bariloche por R. Nac. 237";
  private static final String dwcVerbatimLatitude = "latitude 12.123456";
  private static final String dwcVerbatimLongitude = "long 45.01";
  private static final String dwcVerbatimCoordinateSystem = "decimal degrees";
  private static final String dwcVerbatimSRS = "EPSG:4326";
  private static final String dwcVerbatimElevation = "100-200 m";
  private static final String dwcVerbatimDepth = "10-20 m ";
  private static final String[] dwcOtherRecordNumbers = new String[]{"80-79", "80-80"};
  private static final String dwcCountry = "Atlantis";
  private static final String dwcCountryCode = "Al";
  private static final String dwcStateProvince = "Island of Pharo's";
  private static final CollectingEvent.GeographicPlaceNameSource geographicPlaceNameSource = CollectingEvent.GeographicPlaceNameSource.OSM;
  private static final String geographicPlaceName = "Morocco";
  public static final LocalDateTime TEST_DATE_TIME = LocalDateTime.of(2000, 2, 3, 0, 0);
  private static GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail;
  private CollectingEvent collectingEvent;
  private static final String habitat = "Desert";

  @SneakyThrows
  @BeforeAll
  static void beforeAll() {
    geographicPlaceNameSourceDetail = GeographicPlaceNameSourceDetail.builder()
      .sourceID("1")
      .sourceIdType("N")
      .sourceUrl(new URL("https://github.com/orgs/AAFC-BICoE/dashboard")).build();
  }

  @BeforeEach
  void setUp() {
    geoReferenceAssertion = newAssertion(12.123456);
    geoReferenceAssertion.setIsPrimary(true);

    collectingEvent = CollectingEventFactory.newCollectingEvent()
      .geoReferenceAssertions(Collections.singletonList((geoReferenceAssertion)))
      .startEventDateTime(TEST_DATE_TIME)
      .startEventDateTimePrecision((byte) 8)
      .dwcRecordedBy(dwcRecordedBy)
      .dwcVerbatimLocality(dwcVerbatimLocality)
      .dwcVerbatimLatitude(dwcVerbatimLatitude)
      .dwcVerbatimLongitude(dwcVerbatimLongitude)
      .dwcVerbatimCoordinateSystem(dwcVerbatimCoordinateSystem)
      .dwcVerbatimSRS(dwcVerbatimSRS)
      .dwcVerbatimElevation(dwcVerbatimElevation)
      .dwcVerbatimDepth(dwcVerbatimDepth)
      .dwcOtherRecordNumbers(dwcOtherRecordNumbers)
      .dwcCountry(dwcCountry)
      .habitat(habitat)
      .dwcCountryCode(dwcCountryCode)
      .dwcStateProvince(dwcStateProvince)
      .geographicPlaceNameSource(geographicPlaceNameSource)
      .geographicPlaceName(geographicPlaceName)
      .geographicPlaceNameSourceDetail(geographicPlaceNameSourceDetail)
      .uuid(UUID.randomUUID())
      .build();
    assertNull(collectingEvent.getId());
    collectingEventService.create(collectingEvent);
  }

  @Test
  public void nullStartTimeNonNullEndTime_throwsIllegalArgumentException() {
    collectingEvent = CollectingEventFactory.newCollectingEvent()
      .endEventDateTime(LocalDateTime.of(2008, 1, 1, 1, 1, 1))
      .build();
    ValidationException exception = assertThrows(
      ValidationException.class,
      () -> collectingEventService.create(collectingEvent));

    String expectedMessage = "The start and end dates do not create a valid timeline";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  public void startTimeAfterEndTime_throwsIllegalArgumentException() {
    collectingEvent = CollectingEventFactory.newCollectingEvent()
      .startEventDateTime(LocalDateTime.of(2009, 1, 1, 1, 1, 1))
      .endEventDateTime(LocalDateTime.of(2008, 1, 1, 1, 1, 1))
      .build();
    ValidationException exception = assertThrows(
      ValidationException.class,
      () -> collectingEventService.create(collectingEvent));

    String expectedMessage = "The start and end dates do not create a valid timeline";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void create() {
    //Assert generated fields
    assertNotNull(collectingEvent.getId());
    assertNotNull(collectingEvent.getCreatedOn());
    assertNotNull(collectingEvent.getUuid());
  }

  @Test
  void create_WithInvalidGeo_throwsIllegalArgumentException() {
    collectingEvent.getGeoReferenceAssertions().forEach(geo -> {
      geo.setDwcGeoreferenceVerificationStatus(
        GeoreferenceAssertion.GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE);
      geo.setDwcDecimalLatitude(2.0);
    });

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> collectingEventService.create(collectingEvent));

    String expectedMessage = "dwcDecimalLatitude, dwcDecimalLongitude and dwcCoordinateUncertaintyInMeters must be null if dwcGeoreferenceVerificationStatus is GEOREFERENCING_NOT_POSSIBLE";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void create_WithInvalidGeoPrimaries_ValidationException() {
    final CollectingEvent eventToManyPrimaries = newEvent();

    GeoreferenceAssertion geo = newAssertion(1);
    geo.setIsPrimary(true);
    GeoreferenceAssertion geo2 = newAssertion(1);
    geo2.setIsPrimary(true);

    eventToManyPrimaries.setGeoReferenceAssertions(List.of(geo, geo2));
    assertThrows(ValidationException.class, () -> collectingEventService.create(eventToManyPrimaries));

    final CollectingEvent noPrimaries = newEvent();
    geo.setIsPrimary(false);
    geo2.setIsPrimary(false);
    noPrimaries.setGeoReferenceAssertions(List.of(geo, geo2));
    assertThrows(ValidationException.class, () -> collectingEventService.create(noPrimaries));

    final CollectingEvent singleNonPrimary = newEvent();
    geo.setIsPrimary(false);
    singleNonPrimary.setGeoReferenceAssertions(List.of(geo));
    assertThrows(ValidationException.class, () -> collectingEventService.create(singleNonPrimary));

    final CollectingEvent singleValid = newEvent();
    geo.setIsPrimary(true);
    singleValid.setGeoReferenceAssertions(List.of(geo));
    assertDoesNotThrow(() -> collectingEventService.create(singleValid));
  }

  @Test
  void find() {
    CollectingEvent fetchedCollectingEvent = dbService
      .find(CollectingEvent.class, collectingEvent.getId());
    assertEquals(collectingEvent.getId(), fetchedCollectingEvent.getId());
    assertEquals(TEST_DATE_TIME, fetchedCollectingEvent.getStartEventDateTime());
    assertEquals((byte) 8, fetchedCollectingEvent.getStartEventDateTimePrecision());
    assertEquals(dwcRecordedBy, fetchedCollectingEvent.getDwcRecordedBy());
    assertEquals(
      geoReferenceAssertion.getDwcDecimalLatitude(),
      fetchedCollectingEvent.getGeoReferenceAssertions().iterator().next().getDwcDecimalLatitude());
    assertNotNull(fetchedCollectingEvent.getCreatedOn());
    assertEquals(dwcVerbatimLocality, fetchedCollectingEvent.getDwcVerbatimLocality());
    assertEquals(dwcVerbatimLatitude, fetchedCollectingEvent.getDwcVerbatimLatitude());
    assertEquals(dwcVerbatimLongitude, fetchedCollectingEvent.getDwcVerbatimLongitude());
    assertEquals(
      dwcVerbatimCoordinateSystem,
      fetchedCollectingEvent.getDwcVerbatimCoordinateSystem());
    assertEquals(dwcVerbatimSRS, fetchedCollectingEvent.getDwcVerbatimSRS());
    assertEquals(dwcVerbatimElevation, fetchedCollectingEvent.getDwcVerbatimElevation());
    assertEquals(dwcVerbatimDepth, fetchedCollectingEvent.getDwcVerbatimDepth());
    assertArrayEquals(dwcOtherRecordNumbers, fetchedCollectingEvent.getDwcOtherRecordNumbers());
    assertEquals(dwcCountry, fetchedCollectingEvent.getDwcCountry());
    assertEquals(dwcCountryCode, fetchedCollectingEvent.getDwcCountryCode());
    assertEquals(dwcStateProvince, fetchedCollectingEvent.getDwcStateProvince());
    assertEquals(geographicPlaceName, fetchedCollectingEvent.getGeographicPlaceName());
    assertEquals(geographicPlaceNameSource, fetchedCollectingEvent.getGeographicPlaceNameSource());
    assertEquals(
      geographicPlaceNameSourceDetail.getSourceID(),
      fetchedCollectingEvent.getGeographicPlaceNameSourceDetail().getSourceID());
    assertNotNull(fetchedCollectingEvent.getGeographicPlaceNameSourceDetail().getSourceUrl());
    assertEquals(
      geographicPlaceNameSourceDetail.getSourceIdType(),
      fetchedCollectingEvent.getGeographicPlaceNameSourceDetail().getSourceIdType());
    assertEquals(habitat, fetchedCollectingEvent.getHabitat());
  }

  @Test
  void delete_Orphans_Removed() {
    collectingEventService.delete(collectingEvent);
    assertNull(dbService.find(CollectingEvent.class, collectingEvent.getId()));
    assertNull(dbService.find(GeoreferenceAssertion.class, geoReferenceAssertion.getId()));
  }

  @Test
  void update_whenGeoAssertionsUpdated_GeosUpdated() {
    CollectingEvent fetchedCollectingEvent = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class);
    GeoreferenceAssertion geo = newAssertion(1);
    geo.setIsPrimary(true);
    GeoreferenceAssertion geo2 = newAssertion(2);

    // Pop one, add two
    fetchedCollectingEvent.setGeoReferenceAssertions(List.of(geo, geo2));

    collectingEventService.update(fetchedCollectingEvent);

    List<GeoreferenceAssertion> results = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class).getGeoReferenceAssertions();
    assertEquals(2, results.size());
    assertEquals(geo.getDwcDecimalLatitude(), results.get(0).getDwcDecimalLatitude());
    assertEquals(geo2.getDwcDecimalLatitude(), results.get(1).getDwcDecimalLatitude());

    fetchedCollectingEvent = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class);

    // remove last
    fetchedCollectingEvent.setGeoReferenceAssertions(List.of(geo));
    collectingEventService.update(fetchedCollectingEvent);

    results = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class).getGeoReferenceAssertions();
    assertEquals(1, results.size());
    assertEquals(geo.getDwcDecimalLatitude(), results.get(0).getDwcDecimalLatitude());
  }

  @Test
  void update_whenGeoAssertionsCleared_GeosRemoved() {
    CollectingEvent fetchedCollectingEvent = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class);

    fetchedCollectingEvent.setGeoReferenceAssertions(null);

    collectingEventService.update(fetchedCollectingEvent);
    CollectingEvent result = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class);

    assertNull(result.getGeoReferenceAssertions());
  }

  @Test
  void update_WithInvalidGeo_throwsIllegalArgumentException() {
    collectingEvent.getGeoReferenceAssertions().forEach(geo -> {
      geo.setDwcGeoreferenceVerificationStatus(
        GeoreferenceAssertion.GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE);
      geo.setDwcDecimalLatitude(2.0);
    });

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> collectingEventService.update(collectingEvent));

    String expectedMessage = "dwcDecimalLatitude, dwcDecimalLongitude and dwcCoordinateUncertaintyInMeters must be null if dwcGeoreferenceVerificationStatus is GEOREFERENCING_NOT_POSSIBLE";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  private static CollectingEvent newEvent() {
    return CollectingEvent.builder()
      .uuid(UUID.randomUUID())
      .createdBy("dina")
      .group("group")
      .startEventDateTime(LocalDateTime.now().minusDays(1))
      .build();
  }

  private static GeoreferenceAssertion newAssertion(double latitude) {
    return GeoreferenceAssertionFactory.newGeoreferenceAssertion()
      .dwcDecimalLatitude(latitude)
      .isPrimary(false)
      .dwcDecimalLongitude(45.01)
      .build();
  }

}
