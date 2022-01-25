package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.CustomViewDto;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Map;

public final class CustomViewFixture {

  public static final String GROUP = "aafc";

  private CustomViewFixture() {
    //utility class
  }

  public static CustomViewDto.CustomViewDtoBuilder newCustomView() {
    return CustomViewDto.builder()
        .group(GROUP)
        .restrictToCreatedBy(false)
        .viewConfiguration(Map.of())
        .name(RandomStringUtils.randomAlphabetic(12));
  }
}
