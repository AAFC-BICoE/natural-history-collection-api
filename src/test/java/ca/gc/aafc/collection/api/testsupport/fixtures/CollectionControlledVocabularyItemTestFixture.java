package ca.gc.aafc.collection.api.testsupport.fixtures;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyItemDto;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

public class CollectionControlledVocabularyItemTestFixture {

  public static CollectionControlledVocabularyItemDto newCollectionControlledVocabularyItem() {
    CollectionControlledVocabularyItemDto collectionControlledVocabularyItemDto = new CollectionControlledVocabularyItemDto();
    collectionControlledVocabularyItemDto.setName(RandomStringUtils.randomAlphabetic(5));
    collectionControlledVocabularyItemDto.setVocabularyElementType(
      TypedVocabularyElement.VocabularyElementType.INTEGER);
    collectionControlledVocabularyItemDto.setAcceptedValues(new String[]{"1", "2"});
    collectionControlledVocabularyItemDto.setUnit("cm");
    collectionControlledVocabularyItemDto.setCreatedBy("created by");
    collectionControlledVocabularyItemDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    return collectionControlledVocabularyItemDto;
  }

}
