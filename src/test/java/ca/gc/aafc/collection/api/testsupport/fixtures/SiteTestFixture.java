package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.lang3.RandomStringUtils;
import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;

public class SiteTestFixture {
  private static final String GROUP = "aafc";
  private static final String CODE = "LTAE-M";

  public static SiteDto newSite() {
    SiteDto siteDto = new SiteDto();
    siteDto.setName(RandomStringUtils.randomAlphabetic(5));
    siteDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    siteDto.setCode(CODE);
    siteDto.setGroup(GROUP);

    siteDto.setAttachment(List.of(
        ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("file").build()));

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

}
