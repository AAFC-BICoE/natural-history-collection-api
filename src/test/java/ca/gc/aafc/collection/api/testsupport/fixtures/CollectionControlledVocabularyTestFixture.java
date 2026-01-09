package ca.gc.aafc.collection.api.testsupport.fixtures;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyDto;
import ca.gc.aafc.dina.entity.ControlledVocabulary;

public class CollectionControlledVocabularyTestFixture {

  public static CollectionControlledVocabularyDto newCollectionControlledVocabulary() {
    CollectionControlledVocabularyDto collectionControlledVocabularyItemDto = new CollectionControlledVocabularyDto();
    collectionControlledVocabularyItemDto.setName(RandomStringUtils.randomAlphabetic(5));
    collectionControlledVocabularyItemDto.setType(ControlledVocabulary.ControlledVocabularyType.SYSTEM);
    collectionControlledVocabularyItemDto.setVocabClass(ControlledVocabulary.ControlledVocabularyClass.QUALIFIED_VALUE);
    collectionControlledVocabularyItemDto.setCreatedBy("created by");
    collectionControlledVocabularyItemDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    return collectionControlledVocabularyItemDto;
  }

}
