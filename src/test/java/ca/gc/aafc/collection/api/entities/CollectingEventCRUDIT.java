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

  private final GeoreferenceAssertion geoReferenceAssertion = newAssertion(12.123456);

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
  public void create() {
    //Assert generated fields
    assertNotNull(collectingEvent.getId());
    assertNotNull(collectingEvent.getCreatedOn());
    assertNotNull(collectingEvent.getUuid());
  }

  @Test
  public void find() {
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
    GeoreferenceAssertion geo2 = newAssertion(2);

    fetchedCollectingEvent.setGeoReferenceAssertions(List.of(geo, geo2));

    collectingEventService.update(fetchedCollectingEvent);
    CollectingEvent result = collectingEventService
      .findOne(collectingEvent.getUuid(), CollectingEvent.class);

    List<GeoreferenceAssertion> list = result.getGeoReferenceAssertions();
    assertEquals(2, list.size());
    assertEquals(geo.getDwcDecimalLatitude(), list.get(0).getDwcDecimalLatitude());
    assertEquals(geo2.getDwcDecimalLatitude(), list.get(1).getDwcDecimalLatitude());
  }

  private static GeoreferenceAssertion newAssertion(double latitude) {
    return GeoreferenceAssertionFactory.newGeoreferenceAssertion()
      .dwcDecimalLatitude(latitude)
      .dwcDecimalLongitude(45.01)
      .build();
  }
}
