package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.dina.i18n.MultilingualTitle;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import java.util.List;

public class MultilingualTestFixture {

  public static MultilingualDescription newMultilingualDescription() {
    return MultilingualDescription.builder()
            .descriptions(List.of(MultilingualDescription
                    .MultilingualPair.of("en", RandomStringUtils.randomAlphabetic(4))))
            .build();
  }

  public static MultilingualTitle newMultilingualTitle() {
    return MultilingualTitle.builder()
            .titles(List.of(MultilingualTitle
                    .MultilingualTitlePair.of("en", RandomStringUtils.randomAlphabetic(4))))
            .build();
  }
}
