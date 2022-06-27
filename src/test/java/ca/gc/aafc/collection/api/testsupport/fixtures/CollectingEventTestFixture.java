package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.GeoreferenceAssertionDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.ExtensionValue;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail.SourceAdministrativeLevel;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import lombok.SneakyThrows;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CollectingEventTestFixture {

  public static final String XI_02_1798 = "XI-02-1798";
  public static final String VER_COOR = "26.089, 106.36";

  public static final LocalDate START_DATE = LocalDate.of(2000, 1, 1);

  public static final LocalDate END_DATE = LocalDate.of(2002, 10, 10);

  public static final String DWC_RECORDED_BY = "Julian Grant | Noah Hart";
  public static final String DWC_VERBATIM_LOCALITY = "25 km NNE Bariloche por R. Nac. 237";
  public static final String DWC_VERBATIM_LATITUDE = "latitude 12.123456";
  public static final String DWC_VERBATIM_LONGITUDE = "long 45.01";
  public static final String DWC_VERBATIM_COORDINATE_SYSTEM = "decimal degrees";
  public static final String DWC_VERBATIM_SRS = "EPSG:4326";
  public static final String DWC_VERBATIM_ELEVATION = "100-200 m";
  public static final String DWC_VERBATIM_DEPTH = "10-20 m ";
  public static final BigDecimal DWC_MAXIMUM_ELEVATION_IN_METERS = new BigDecimal("100.01");
  public static final BigDecimal DWC_MAXIMUM_DEPTH_IN_METERS = new BigDecimal("20.02");
  public static final BigDecimal DWC_MINIMUM_ELEVATION_IN_METERS = new BigDecimal("50.50");
  public static final BigDecimal DWC_MINIMUM_DEPTH_IN_METERS = new BigDecimal("10.10");
  public static final String SUBSTRATE = RandomStringUtils.randomAlphabetic(58);
  public static final String REMARKS = "this is a remark";
  public static final Boolean PUBLICLY_RELEASABLE = false;
  public static final String NOT_PUBLICLY_RELEASABLE_REASON = "because it is not allowed";
  public static final String[] TAGS = new String[]{"0-Tag", "1-Tag"};

  public static final LocalDate TEST_GEOREFERENCE_DATE = LocalDate.now();
  public static final String HOST = "host";
  public static final CollectingEvent.GeographicPlaceNameSource GEOGRAPHIC_PLACE_NAME_SOURCE = CollectingEvent.GeographicPlaceNameSource.OSM;
  public static final GeoreferenceAssertionDto GEOREFERENCE_ASSERTION_DTO = GeoreferenceAssertionDto.builder()
    .dwcDecimalLatitude(12.123456)
    .dwcDecimalLongitude(45.01)
    .dwcGeoreferencedDate(TEST_GEOREFERENCE_DATE)
    .isPrimary(true)
    .georeferencedBy(Collections.singletonList(UUID.randomUUID()))
    .literalGeoreferencedBy("dina literal by")
    .dwcGeoreferenceSources("source")
    .dwcGeoreferenceRemarks("remarks")
    .dwcGeoreferenceProtocol("protocol")
    .dwcGeoreferenceVerificationStatus(null)
    .dwcGeodeticDatum("datum")
    .dwcCoordinateUncertaintyInMeters(2)
    .build();

  public static final GeographicPlaceNameSourceDetail.Country TEST_COUNTRY = GeographicPlaceNameSourceDetail.Country
      .builder().code("Al").name("Atlantis")
      .build();

  public static final SourceAdministrativeLevel TEST_PROVINCE = SourceAdministrativeLevel.builder().id("A32F")
      .element("N").placeType("province").name("Island of Pharo's")
      .build();

  public static final SourceAdministrativeLevel SELECTED_GEOGRAPHIC_PLACE = SourceAdministrativeLevel.builder()
      .id("A32B")
      .element("R")
      .placeType("province")
      .name("Ontario")
      .build();

  public static final String[] DWC_OTHER_RECORD_NUMBERS = new String[]{"80-79", "80-80"};
  public static final String HABITAT = "Tropical";

  public static final ExtensionValue EXTENSION_VALUE = ExtensionValue.builder()
    .extKey("mixs_soil_v5")
    .extVersion("v5")
    .extTerm("experimental_factor")
    .value("definition of experimentWal factor")
    .build();

  @SneakyThrows
  public static GeographicPlaceNameSourceDetail newGeographicPlaceNameSourceDetail() {
    return GeographicPlaceNameSourceDetail.builder()
        .selectedGeographicPlace(SELECTED_GEOGRAPHIC_PLACE)
        .country(TEST_COUNTRY)
        .stateProvince(TEST_PROVINCE)
        .sourceUrl("https://github.com/orgs/AAFC-BICoE/dashboard")
        // recordedOn should be overwritten by the server side generated value
        .recordedOn(OffsetDateTime.of(
          LocalDateTime.of(2000, 1, 1, 11, 10),
          ZoneOffset.ofHoursMinutes(1, 0)))
        .build();
  }

  public static CollectingEventDto newEventDto() {
    List<GeoreferenceAssertionDto> assertions = new ArrayList<>();
    assertions.add(GEOREFERENCE_ASSERTION_DTO);
    List<ExtensionValue> extensionValues = new ArrayList<>();
    extensionValues.add(EXTENSION_VALUE);

    CollectingEventDto ce = new CollectingEventDto();
    ce.setGroup("aafc");
    ce.setCreatedBy("test user");
    ce.setDwcFieldNumber("abcd");
    ce.setStartEventDateTime(START_DATE.toString());
    ce.setEndEventDateTime(END_DATE.toString());
    ce.setDwcRecordedBy(DWC_RECORDED_BY);
    ce.setVerbatimEventDateTime(XI_02_1798);
    ce.setDwcVerbatimCoordinates(VER_COOR);
    ce.setGeographicPlaceNameSourceDetail(newGeographicPlaceNameSourceDetail());
    ce.setGeographicPlaceNameSource(GEOGRAPHIC_PLACE_NAME_SOURCE);
    ce.setGeoReferenceAssertions(assertions);
    ce.setAttachment(List.of(
      ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("file").build()));
    ce.setCollectors(
      List.of(ExternalRelationDto.builder().type("agent").id(UUID.randomUUID().toString()).build()));
    ce.setDwcVerbatimLocality(DWC_VERBATIM_LOCALITY);
    ce.setDwcVerbatimLatitude(DWC_VERBATIM_LATITUDE);
    ce.setDwcVerbatimLongitude(DWC_VERBATIM_LONGITUDE);
    ce.setDwcVerbatimCoordinateSystem(DWC_VERBATIM_COORDINATE_SYSTEM);
    ce.setDwcVerbatimSRS(DWC_VERBATIM_SRS);
    ce.setDwcVerbatimElevation(DWC_VERBATIM_ELEVATION);
    ce.setDwcVerbatimDepth(DWC_VERBATIM_DEPTH);
    ce.setDwcOtherRecordNumbers(DWC_OTHER_RECORD_NUMBERS);
    ce.setHabitat(HABITAT);
    ce.setHost(HOST);
    ce.setDwcMaximumDepthInMeters(DWC_MAXIMUM_DEPTH_IN_METERS);
    ce.setDwcMaximumElevationInMeters(DWC_MAXIMUM_ELEVATION_IN_METERS);
    ce.setDwcMinimumDepthInMeters(DWC_MINIMUM_DEPTH_IN_METERS);
    ce.setDwcMinimumElevationInMeters(DWC_MINIMUM_ELEVATION_IN_METERS);
    ce.setSubstrate(SUBSTRATE);
    ce.setRemarks(REMARKS);
    ce.setPubliclyReleasable(PUBLICLY_RELEASABLE);
    ce.setNotPubliclyReleasableReason(NOT_PUBLICLY_RELEASABLE_REASON);
    ce.setTags(TAGS);
    ce.setExtensionValues(extensionValues);
    return ce;
  }

}
