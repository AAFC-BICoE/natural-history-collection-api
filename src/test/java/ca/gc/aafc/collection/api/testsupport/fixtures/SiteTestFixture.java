package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail;
import ca.gc.aafc.collection.api.entities.GeographicPlaceNameSourceDetail.SourceAdministrativeLevel;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import lombok.SneakyThrows;

public class SiteTestFixture {
  private static final String GROUP = "aafc";
  private static final String CODE = "LTAE-M";
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

  public static SiteDto newSite() {
    SiteDto siteDto = new SiteDto();
    siteDto.setName(RandomStringUtils.randomAlphabetic(5));
    siteDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    siteDto.setCode(CODE);
    siteDto.setGroup(GROUP);

    siteDto.setAttachment(List.of(
        ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("file").build()));

    siteDto.setGeographicPlaceNameSource(CollectingEvent.GeographicPlaceNameSource.OSM);
    siteDto.setGeographicPlaceNameSourceDetail(newGeographicPlaceNameSourceDetail());

    siteDto.setDwcCountry("Canada");
    siteDto.setDwcCountryCode("CA");
    siteDto.setDwcStateProvince("Ontario");
    return siteDto;
  }

  public static Map<String, Object> polygonGeoJson() {
    return Map.of(
        "type", "Polygon",
        "coordinates", List.of(
            List.of(
                List.of(100.0, 0.0),
                List.of(101.0, 0.0),
                List.of(101.0, 1.0),
                List.of(100.0, 1.0),
                List.of(100.0, 0.0))));
  }

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

}
