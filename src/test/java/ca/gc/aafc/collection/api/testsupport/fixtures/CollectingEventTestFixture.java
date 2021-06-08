package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import lombok.SneakyThrows;

public class CollectingEventTestFixture {

  
  public static final String XI_02_1798 = "XI-02-1798";
  public static final String VER_COOR = "26.089, 106.36";

  public static final LocalDate startDate = LocalDate.of(2000, 1, 1);

  public static final LocalDate endDate = LocalDate.of(2002, 10, 10);

  public static final String dwcRecordedBy = "Julian Grant | Noah Hart";
  public static final String dwcVerbatimLocality = "25 km NNE Bariloche por R. Nac. 237";
  public static final String dwcVerbatimLatitude = "latitude 12.123456";
  public static final String dwcVerbatimLongitude = "long 45.01";
  public static final String dwcVerbatimCoordinateSystem = "decimal degrees";
  public static final String dwcVerbatimSRS = "EPSG:4326";
  public static final String dwcVerbatimElevation = "100-200 m";
  public static final String dwcVerbatimDepth = "10-20 m ";
  public static final LocalDate testGeoreferencedDate = LocalDate.now();
  public static final CollectingEvent.GeographicPlaceNameSource geographicPlaceNameSource = CollectingEvent.GeographicPlaceNameSource.OSM;
  public static final GeoreferenceAssertionDto geoReferenceAssertion = GeoreferenceAssertionDto.builder()
    .dwcDecimalLatitude(12.123456)
    .dwcDecimalLongitude(45.01)
    .dwcGeoreferencedDate(testGeoreferencedDate)
    .isPrimary(true)
    .build();
  
  public static final GeographicPlaceNameSourceDetail.Country TEST_COUNTRY =
    GeographicPlaceNameSourceDetail.Country.builder().code("Al").name("Atlantis")
    .build();

  public static final GeographicPlaceNameSourceDetail.SourceAdministrativeLevel TEST_PROVINCE =
    GeographicPlaceNameSourceDetail.SourceAdministrativeLevel.builder().id("A32F")
    .element("N").placeType("province").name("Island of Pharo's")
    .build();   

  public static final String[] dwcOtherRecordNumbers = new String[]{"80-79", "80-80"};
  public static final String habitat = "Tropical";

  @SneakyThrows
  public static GeographicPlaceNameSourceDetail newGeographicPlaceNameSourceDetail() {
    return GeographicPlaceNameSourceDetail.builder()
    .country(TEST_COUNTRY)
    .stateProvince(TEST_PROVINCE)
    .sourceUrl(new URL("https://github.com/orgs/AAFC-BICoE/dashboard"))
    // recordedOn should be overwritten by the server side generated value
    .recordedOn(OffsetDateTime.of(
      LocalDateTime.of(2000, 1, 1, 11, 10),
      ZoneOffset.ofHoursMinutes(1, 0)))
    .build();
  }

  public static CollectingEventDto newEventDto(String startDateTime, String endDateTime) {
    CollectingEventDto ce = newEventDto();
    ce.setStartEventDateTime(ISODateTime.parse(startDateTime).toString());
    ce.setEndEventDateTime(ISODateTime.parse(endDateTime).toString());
    return ce;
  }

  public static CollectingEventDto newEventDto() {
    CollectingEventDto ce = new CollectingEventDto();
    ce.setGroup("aafc");
    ce.setCreatedBy("test user");
    ce.setStartEventDateTime(startDate.toString());
    ce.setEndEventDateTime(endDate.toString());
    ce.setDwcRecordedBy(dwcRecordedBy);
    ce.setVerbatimEventDateTime(XI_02_1798);
    ce.setDwcVerbatimCoordinates(VER_COOR);
    ce.setGeographicPlaceNameSourceDetail(newGeographicPlaceNameSourceDetail());
    ce.setGeographicPlaceNameSource(geographicPlaceNameSource);
    ce.setGeoReferenceAssertions(List.of(geoReferenceAssertion));
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
    ce.setHabitat(habitat);
    return ce;
  }
  
}
