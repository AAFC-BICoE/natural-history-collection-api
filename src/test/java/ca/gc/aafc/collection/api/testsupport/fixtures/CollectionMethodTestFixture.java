package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.CollectionMethodDto;
import org.apache.commons.lang3.RandomStringUtils;

public class CollectionMethodTestFixture {

  public static CollectionMethodDto newMethod() {
    return CollectionMethodDto.builder()
      .name(RandomStringUtils.randomAlphabetic(4))
      .createdBy(RandomStringUtils.randomAlphabetic(4))
      .group("aafc")
      .multilingualDescription(MultilingualTestFixture.newMultilingualDescription())
      .build();
  }

}
