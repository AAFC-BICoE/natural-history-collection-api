package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.dina.i18n.MultilingualDescription;

import java.util.List;

public class MultilingualDescriptionFactory {

  public static final MultilingualDescription.MultilingualPair MULTILINGUAL_PAIR_FR =
          MultilingualDescription.MultilingualPair.of("fr", "description en français");

  public static final MultilingualDescription.MultilingualPair MULTILINGUAL_PAIR_EN =
          MultilingualDescription.MultilingualPair.of("en", "description in english");

  public static MultilingualDescription newMultilingualDescription() {
    return MultilingualDescription.builder()
            .descriptions(List.of(MULTILINGUAL_PAIR_EN, MULTILINGUAL_PAIR_FR))
            .build();
  }

}
