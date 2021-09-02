package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.CollectionDto;
import org.apache.commons.lang3.RandomStringUtils;

public final class CollectionFixture {

  private CollectionFixture() {
  }

  public static CollectionDto newCollection() {
    return CollectionDto.builder()
      .code(RandomStringUtils.randomAlphabetic(4))
      .name(RandomStringUtils.randomAlphabetic(3))
      .createdBy(RandomStringUtils.randomAlphabetic(3))
      .group(RandomStringUtils.randomAlphabetic(4))
      .build();
  }
}
