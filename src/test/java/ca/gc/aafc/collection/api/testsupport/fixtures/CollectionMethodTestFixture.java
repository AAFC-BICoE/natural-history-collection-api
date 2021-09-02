package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.CollectionMethodDto;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import java.util.List;

public class CollectionMethodTestFixture {

  public static CollectionMethodDto newMethod() {
    return CollectionMethodDto.builder()
      .name(RandomStringUtils.randomAlphabetic(4))
      .createdBy(RandomStringUtils.randomAlphabetic(4))
      .group("aafc")
      .multilingualDescription(newMulti())
      .build();
  }

  private static MultilingualDescription newMulti() {
    return MultilingualDescription.builder()
      .descriptions(List.of(MultilingualDescription.MultilingualPair.builder()
        .desc(RandomStringUtils.randomAlphabetic(4))
        .lang("en")
        .build()))
      .build();
  }

}
