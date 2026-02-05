package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.time.LocalDate;
import org.apache.commons.lang3.RandomStringUtils;
import ca.gc.aafc.collection.api.dto.SiteDto;

public class SiteTestFixture {
  private static final String GROUP = "aafc";
  private static final LocalDate START_DATE = LocalDate.of(1991, 01, 01);
  private static final LocalDate END_DATE = LocalDate.now();
  private static final String CODE = "LTAE-M";

  public static SiteDto newSite() {
    SiteDto siteDto = new SiteDto();
    siteDto.setName(RandomStringUtils.randomAlphabetic(5));
    siteDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    siteDto.setStartDate(START_DATE);
    siteDto.setEndDate(END_DATE);
    siteDto.setCode(CODE);
    siteDto.setGroup(GROUP);

    return siteDto;
  }

}
