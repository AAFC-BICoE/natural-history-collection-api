package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.CollectionManagedAttributeFactory;
import ca.gc.aafc.dina.entity.ManagedAttribute.ManagedAttributeType;
import lombok.SneakyThrows;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CollectingEventCRUDIT extends CollectionModuleBaseIT {

  private GeoreferenceAssertionDto geoReferenceAssertion;

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
  private static final BigDecimal dwcMinimumElevationInMeters = new BigDecimal("11.11");
  private static final BigDecimal dwcMinimumDepthInMeters = new BigDecimal("10.32");

  private static final GeographicPlaceNameSourceDetail.Country TEST_COUNTRY =
      GeographicPlaceNameSourceDetail.Country.builder().code("Al").name("Atlantis")
          .build();
  private static final GeographicPlaceNameSourceDetail.SourceAdministrativeLevel TEST_PROVINCE =
      GeographicPlaceNameSourceDetail.SourceAdministrativeLevel.builder().id("A32F")
          .element("N").placeType("province").name("Island of Pharo's")
          .build();

  private static final CollectingEvent.GeographicPlaceNameSource geographicPlaceNameSource = CollectingEvent.GeographicPlaceNameSource.OSM;

  public static final LocalDateTime TEST_DATE_TIME = LocalDateTime.of(2000, 2, 3, 0, 0);
  private static GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail;
  private CollectingEvent collectingEvent;
  private static final String habitat = "Desert";

  @SneakyThrows
  @BeforeAll
  static void beforeAll() {
    geographicPlaceNameSourceDetail = GeographicPlaceNameSourceDetail
        .builder()
        .country(TEST_COUNTRY)
        .stateProvince(TEST_PROVINCE)
        .sourceUrl(new URL("https://github.com/orgs/AAFC-BICoE/dashboard")).build();
  }

  @BeforeEach
  void setUp() {
    geoReferenceAssertion = GeoreferenceAssertionDto.builder()
      .dwcDecimalLatitude(12.123456)
      .isPrimary(true)
      .build();

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
      .substrate(RandomStringUtils.randomAlphabetic(3))
      .dwcCountry(dwcCountry)
      .habitat(habitat)
      .dwcCountryCode(dwcCountryCode)
      .dwcStateProvince(dwcStateProvince)
      .geographicPlaceNameSource(geographicPlaceNameSource)
      .geographicPlaceNameSourceDetail(geographicPlaceNameSourceDetail)
      .dwcMinimumElevationInMeters(dwcMinimumElevationInMeters)
      .dwcMinimumDepthInMeters(dwcMinimumDepthInMeters)
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
        GeoreferenceAssertionDto.GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE);
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
  void geographicPlaceNameSourceDetail_ConstraintValidation_ConstraintViolationExceptionThrown() {
    collectingEvent.setGeographicPlaceNameSourceDetail(GeographicPlaceNameSourceDetail.builder().build());
    Assertions.assertThrows(
      ConstraintViolationException.class,
      () -> collectingEventService.create(collectingEvent));
  }

  @SneakyThrows
  @Test
  void geoDetailStateProvince_HasConstraintValidation_ConstraintViolationExceptionThrown() {
    GeographicPlaceNameSourceDetail.SourceAdministrativeLevel invalid = GeographicPlaceNameSourceDetail
      .SourceAdministrativeLevel.builder().element("N").id("").placeType("").build();

    GeographicPlaceNameSourceDetail detail = GeographicPlaceNameSourceDetail.builder()
      .sourceUrl(new URL("http://example.com"))
      .stateProvince(invalid)
      .country(TEST_COUNTRY)
      .higherGeographicPlaces(List.of(TEST_PROVINCE))
      .build();
    collectingEvent.setGeographicPlaceNameSourceDetail(detail);

    Assertions.assertThrows(
      ConstraintViolationException.class,
      () -> collectingEventService.create(collectingEvent));
  }

  @SneakyThrows
  @Test
  void geoDetailHigherGeographicPlaces_HasConstraintValidation_ConstraintViolationExceptionThrown() {
    GeographicPlaceNameSourceDetail.SourceAdministrativeLevel invalid = GeographicPlaceNameSourceDetail
      .SourceAdministrativeLevel.builder().element("N").id("").placeType("").build();

    GeographicPlaceNameSourceDetail detail = GeographicPlaceNameSourceDetail.builder()
      .sourceUrl(new URL("http://example.com"))
      .stateProvince(TEST_PROVINCE)
      .country(TEST_COUNTRY)
      .higherGeographicPlaces(List.of(invalid))
      .build();
    collectingEvent.setGeographicPlaceNameSourceDetail(detail);

    Assertions.assertThrows(
      ConstraintViolationException.class,
      () -> collectingEventService.create(collectingEvent));
  }

  @SneakyThrows
  @Test
  void geoDetailCountry_HasConstraintValidation_ConstraintViolationExceptionThrown() {
    GeographicPlaceNameSourceDetail detail = GeographicPlaceNameSourceDetail.builder()
      .sourceUrl(new URL("http://example.com"))
      .stateProvince(TEST_PROVINCE)
      .country(GeographicPlaceNameSourceDetail.Country.builder().code("1").name("name").build())
      .higherGeographicPlaces(List.of(TEST_PROVINCE))
      .build();
    collectingEvent.setGeographicPlaceNameSourceDetail(detail);

    Assertions.assertThrows(
      ConstraintViolationException.class,
      () -> collectingEventService.create(collectingEvent));
  }

  @Test
  void create_WithInvalidGeoPrimaries_ValidationException() {
    final CollectingEvent eventToManyPrimaries = CollectingEventFactory.newCollectingEvent().build();

    GeoreferenceAssertionDto geo = GeoreferenceAssertionDto.builder()
      .dwcDecimalLatitude(1.0)
      .isPrimary(true)
      .build();
    GeoreferenceAssertionDto geo2 =  GeoreferenceAssertionDto.builder()
      .dwcDecimalLatitude(1.0)
      .isPrimary(true)
      .build();

    eventToManyPrimaries.setGeoReferenceAssertions(List.of(geo, geo2));
    assertThrows(ValidationException.class, () -> collectingEventService.create(eventToManyPrimaries));

    geo.setIsPrimary(false);
    geo2.setIsPrimary(false);
    final CollectingEvent noPrimaries = CollectingEventFactory.newCollectingEvent()
      .geoReferenceAssertions(List.of(geo, geo2))
      .build();
    assertThrows(ValidationException.class, () -> collectingEventService.create(noPrimaries));

    geo.setIsPrimary(false);
    final CollectingEvent singleNonPrimary = CollectingEventFactory.newCollectingEvent()
      .geoReferenceAssertions(List.of(geo))
      .build();
    assertThrows(ValidationException.class, () -> collectingEventService.create(singleNonPrimary));

    geo.setIsPrimary(true);
    ArrayList<GeoreferenceAssertionDto> geoReferenceAssertions = new ArrayList<>();
    geoReferenceAssertions.add(geo);
    final CollectingEvent singleValid = CollectingEventFactory.newCollectingEvent()
      .geoReferenceAssertions(geoReferenceAssertions)
      .build();
    assertDoesNotThrow(() -> collectingEventService.create(singleValid));
  }

  @Test
  void find() {
    CollectingEvent fetchedCollectingEvent = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class);
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
    assertEquals(geographicPlaceNameSource, fetchedCollectingEvent.getGeographicPlaceNameSource());
    assertEquals(
      geographicPlaceNameSourceDetail.getCountry(),
      fetchedCollectingEvent.getGeographicPlaceNameSourceDetail().getCountry());
    assertEquals(
        geographicPlaceNameSourceDetail.getStateProvince(),
        fetchedCollectingEvent.getGeographicPlaceNameSourceDetail().getStateProvince());
    assertNotNull(fetchedCollectingEvent.getGeographicPlaceNameSourceDetail().getSourceUrl());
    assertEquals(habitat, fetchedCollectingEvent.getHabitat());
    assertEquals(dwcMinimumDepthInMeters, fetchedCollectingEvent.getDwcMinimumDepthInMeters());
    assertEquals(dwcMinimumElevationInMeters, fetchedCollectingEvent.getDwcMinimumElevationInMeters());
    MatcherAssert.assertThat(collectingEvent.getSubstrate(), Matchers.is(fetchedCollectingEvent.getSubstrate()));
  }

  @Test
  void delete_Orphans_Removed() {
    collectingEventService.delete(collectingEvent);
    assertNull(collectingEventService.findOne(collectingEvent.getUuid(), CollectingEvent.class));
  }

  @Test
  void update_whenGeoAssertionsUpdated_GeosUpdated() {
    CollectingEvent fetchedCollectingEvent = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class);
    GeoreferenceAssertionDto geo = GeoreferenceAssertionDto.builder()
      .dwcDecimalLatitude(1.0).build();
    geo.setIsPrimary(true);
    GeoreferenceAssertionDto geo2 = GeoreferenceAssertionDto.builder()
    .dwcDecimalLatitude(2.0).build();

    // Pop one, add two
    List<GeoreferenceAssertionDto> assertions = new ArrayList<>();
    assertions.add(geo);
    assertions.add(geo2);
    fetchedCollectingEvent.setGeoReferenceAssertions(assertions);

    collectingEventService.update(fetchedCollectingEvent);

    List<GeoreferenceAssertionDto> results = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class).getGeoReferenceAssertions();
    assertEquals(2, results.size());
    assertEquals(geo.getDwcDecimalLatitude(), results.get(0).getDwcDecimalLatitude());
    assertEquals(geo2.getDwcDecimalLatitude(), results.get(1).getDwcDecimalLatitude());

    fetchedCollectingEvent = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class);

    // remove last
    assertions.clear();
    assertions.add(geo);
    fetchedCollectingEvent.setGeoReferenceAssertions(assertions);
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
        GeoreferenceAssertionDto.GeoreferenceVerificationStatus.GEOREFERENCING_NOT_POSSIBLE);
      geo.setDwcDecimalLatitude(2.0);
    });

    IllegalArgumentException exception = assertThrows(
      IllegalArgumentException.class,
      () -> collectingEventService.update(collectingEvent));

    String expectedMessage = "dwcDecimalLatitude, dwcDecimalLongitude and dwcCoordinateUncertaintyInMeters must be null if dwcGeoreferenceVerificationStatus is GEOREFERENCING_NOT_POSSIBLE";
    String actualMessage = exception.getMessage();

    assertTrue(actualMessage.contains(expectedMessage));
  }

  @Test
  void validate_WhenValidStringType() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(null)
      .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    Map<String, String> maMap = new HashMap<>();
    maMap.put(testManagedAttribute.getKey(), "anything");

    collectingEvent.setManagedAttributes(maMap);
    assertDoesNotThrow(() -> collectingEventService.update(collectingEvent));
  }

  @Test
  void validate_WhenInvalidIntegerTypeExceptionThrown() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(null)
      .managedAttributeType(ManagedAttributeType.INTEGER)
      .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    Map<String, String> maMap = new HashMap<>();
    maMap.put(testManagedAttribute.getKey(), "1.2");

    collectingEvent.setManagedAttributes(maMap);
    assertThrows(ValidationException.class, () ->  collectingEventService.update(collectingEvent));
  }

  @Test
  void assignedValueContainedInAcceptedValues_validationPasses() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(new String[]{"val1", "val2"})
      .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    Map<String, String> maMap = new HashMap<>();
    maMap.put(testManagedAttribute.getKey(), testManagedAttribute.getAcceptedValues()[0]);

    collectingEvent.setManagedAttributes(maMap);
    assertDoesNotThrow(() -> collectingEventService.update(collectingEvent));
  }

  @Test
  void assignedValueNotContainedInAcceptedValues_validationPasses() {
    CollectionManagedAttribute testManagedAttribute = CollectionManagedAttributeFactory.newCollectionManagedAttribute()
      .acceptedValues(new String[]{"val1", "val2"})
      .build();

    collectionManagedAttributeService.create(testManagedAttribute);

    Map<String, String> maMap = new HashMap<>();
    maMap.put(testManagedAttribute.getKey(), "val3");
    
    collectingEvent.setManagedAttributes(maMap);
    assertThrows(ValidationException.class, () ->  collectingEventService.update(collectingEvent));
  }

  @Test
  void update_WithInvalidMinimumValue_throwsConstraintViolationException() {
    CollectingEvent fetchedCollectingEvent = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class);

    fetchedCollectingEvent.setDwcMinimumDepthInMeters(new BigDecimal("-1.01"));
    fetchedCollectingEvent.setDwcMinimumElevationInMeters(new BigDecimal("-2.02"));

    assertThrows(ConstraintViolationException.class, 
      () -> collectingEventService.update(fetchedCollectingEvent));

  }

  @Test
  void update_WithInvalidMinimumMaximumValue_throwsConstraintViolationException() {
    CollectingEvent fetchedCollectingEvent = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class);

    fetchedCollectingEvent.setDwcMinimumDepthInMeters(new BigDecimal("15000.01"));
    fetchedCollectingEvent.setDwcMinimumElevationInMeters(new BigDecimal("15000.02"));
    fetchedCollectingEvent.setDwcMaximumDepthInMeters(new BigDecimal("15000.03"));
    fetchedCollectingEvent.setDwcMaximumElevationInMeters(new BigDecimal("15000.04"));

    assertThrows(ConstraintViolationException.class, 
      () -> collectingEventService.update(fetchedCollectingEvent));
      
  }

  @Test
  void update_WithInvalidPrecision_throwsConstraintVIolationException() {
    CollectingEvent fetchedCollectingEvent = collectingEventService
    .findOne(collectingEvent.getUuid(), CollectingEvent.class);

    fetchedCollectingEvent.setDwcMinimumDepthInMeters(new BigDecimal("14000.011"));

    assertThrows(ConstraintViolationException.class, 
      () -> collectingEventService.update(fetchedCollectingEvent));
  }


  @Test
  void update_WithNullMinimumValue_NoExceptionThrown() {
    CollectingEvent fetchedCollectingEvent = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class);

    fetchedCollectingEvent.setDwcMinimumDepthInMeters(null);
    fetchedCollectingEvent.setDwcMinimumElevationInMeters(null);

    collectingEventService.update(fetchedCollectingEvent);
  }

}
