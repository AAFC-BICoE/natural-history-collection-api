package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.GeoreferenceAssertionFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class CollectingEventCRUDIT extends CollectionModuleBaseIT {

  @Inject
  private DatabaseSupportService dbService;

  private final GeoreferenceAssertion geoReferenceAssertion = GeoreferenceAssertionFactory.newGeoreferenceAssertion()
    .dwcDecimalLatitude(12.123456)
    .dwcDecimalLongitude(45.01)
    .build();

  private static final String dwcRecordedBy = "Julian Grant | Noah Hart";
  private static final String dwcVerbatimLocality  = "25 km NNE Bariloche por R. Nac. 237";
  private static final String dwcVerbatimLatitude = "latitude 12.123456";
  private static final String dwcVerbatimLongitude = "long 45.01";
  private static final String dwcVerbatimCoordinateSystem = "decimal degrees";
  private static final String dwcVerbatimSRS = "EPSG:4326";
  private static final String dwcVerbatimElevation = "100-200 m";
  private static final String dwcVerbatimDepth = "10-20 m ";
  private static final String[] dwcOtherRecordNumbers = new String[] { "80-79", "80-80"};
  private static final String dwcCountry = "Atlantis";
  private static final String dwcCountryCode = "Al";
  private static final String dwcStateProvince = "Island of Pharo's";
  private static final String dwcMunicipality = "Morocco";
  private static final CollectingEvent.GeographicPlaceNameSource geographicPlaceNameSource = CollectingEvent.GeographicPlaceNameSource.OSM;
  private static final String geographicPlaceName = "Morocco";
  private static GeographicPlaceNameSourceDetail geographicPlaceNameSourceDetail = null;

  @SneakyThrows
  @BeforeAll
  static void beforeAll() {
    geographicPlaceNameSourceDetail = GeographicPlaceNameSourceDetail.builder()
      .sourceID("1")
      .sourceIdType("N")
      .sourceUrl(new URL("https://github.com/orgs/AAFC-BICoE/dashboard")).build();
  }

  @Test
  public void testSave() {
    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
       .build();
    collectingEvent.setUuid(UUID.randomUUID());
    geoReferenceAssertion.setUuid(UUID.randomUUID());
    dbService.save(geoReferenceAssertion,false);
    collectingEvent.setOtherGeoReferenceAssertions(Collections.singletonList(geoReferenceAssertion));
    collectingEvent.setPrimaryGeoreferenceAssertion(geoReferenceAssertion);
    assertNull(collectingEvent.getId());
    dbService.save(collectingEvent, false);
    assertNotNull(collectingEvent.getId());
  }

  @Test
  public void testFind() {
    LocalDateTime testDateTime = LocalDateTime.of(2000,2,3,0,0);
    geoReferenceAssertion.setUuid(UUID.randomUUID());
    dbService.save(geoReferenceAssertion,false);
    CollectingEvent collectingEvent = CollectingEventFactory.newCollectingEvent()
        .otherGeoReferenceAssertions(Collections.singletonList(geoReferenceAssertion))
        .primaryGeoreferenceAssertion(geoReferenceAssertion)        
        .startEventDateTime(testDateTime)
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
        .dwcCountryCode(dwcCountryCode)
        .dwcStateProvince(dwcStateProvince)
        .geographicPlaceNameSource(geographicPlaceNameSource)
        .geographicPlaceName(geographicPlaceName)
        .geographicPlaceNameSourceDetail(geographicPlaceNameSourceDetail)
        .uuid(UUID.randomUUID())
        .build();
    dbService.save(collectingEvent,false);

    CollectingEvent fetchedCollectingEvent = dbService
        .find(CollectingEvent.class, collectingEvent.getId());
    assertEquals(collectingEvent.getId(), fetchedCollectingEvent.getId());
    assertEquals(testDateTime, fetchedCollectingEvent.getStartEventDateTime());
    assertEquals((byte) 8, fetchedCollectingEvent.getStartEventDateTimePrecision());
    assertEquals(dwcRecordedBy, fetchedCollectingEvent.getDwcRecordedBy());
    assertEquals(
      geoReferenceAssertion.getId(),
      fetchedCollectingEvent.getOtherGeoReferenceAssertions().iterator().next().getId());
    assertEquals(geoReferenceAssertion.getId(), fetchedCollectingEvent.getPrimaryGeoreferenceAssertion().getId());
    assertEquals(
      12.123456,
      fetchedCollectingEvent.getOtherGeoReferenceAssertions().iterator().next().getDwcDecimalLatitude());
    assertNotNull(fetchedCollectingEvent.getCreatedOn());
    assertEquals(dwcVerbatimLocality, fetchedCollectingEvent.getDwcVerbatimLocality());
    assertEquals(dwcVerbatimLatitude, fetchedCollectingEvent.getDwcVerbatimLatitude());
    assertEquals(dwcVerbatimLongitude, fetchedCollectingEvent.getDwcVerbatimLongitude());
    assertEquals(dwcVerbatimCoordinateSystem,
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
  }

}
