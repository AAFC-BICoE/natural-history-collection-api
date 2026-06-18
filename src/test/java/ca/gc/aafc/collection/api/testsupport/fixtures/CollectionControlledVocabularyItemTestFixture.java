package ca.gc.aafc.collection.api.testsupport.fixtures;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyItemDto;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

public class CollectionControlledVocabularyItemTestFixture {

  public static final String GROUP = "dina";

  public static CollectionControlledVocabularyItemDto newCollectionControlledVocabularyItem() {
    CollectionControlledVocabularyItemDto collectionControlledVocabularyItemDto = new CollectionControlledVocabularyItemDto();
    collectionControlledVocabularyItemDto.setName(RandomStringUtils.randomAlphabetic(5));
    collectionControlledVocabularyItemDto.setVocabularyElementType(
      TypedVocabularyElement.VocabularyElementType.INTEGER);
    collectionControlledVocabularyItemDto.setAcceptedValues(new String[]{"1", "2"});
    collectionControlledVocabularyItemDto.setTerm("the-term");
    collectionControlledVocabularyItemDto.setUnit("cm");
    collectionControlledVocabularyItemDto.setCreatedBy("created by");
    collectionControlledVocabularyItemDto.setGroup("test");
    collectionControlledVocabularyItemDto.setUriTemplate("http://test.org/$1");
    collectionControlledVocabularyItemDto.setDinaComponent(CollectionVocabularyConfiguration.DinaComponent.MATERIAL_SAMPLE.name());
    collectionControlledVocabularyItemDto.setMultilingualTitle(MultilingualTestFixture.newMultilingualTitle());
    collectionControlledVocabularyItemDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    return collectionControlledVocabularyItemDto;
  }

  public static CollectionControlledVocabularyItemDto newCollectionManagedAttribute2() {
    CollectionControlledVocabularyItemDto collectionManagedAttributeDto = new CollectionControlledVocabularyItemDto();
    collectionManagedAttributeDto.setName(RandomStringUtils.randomAlphabetic(5));
    collectionManagedAttributeDto.setGroup(GROUP);
    collectionManagedAttributeDto.setVocabularyElementType(
      TypedVocabularyElement.VocabularyElementType.INTEGER);
    collectionManagedAttributeDto.setAcceptedValues(new String[]{"1", "2"});
    collectionManagedAttributeDto.setUnit("cm");
    collectionManagedAttributeDto.setDinaComponent(CollectionVocabularyConfiguration.DinaComponent.COLLECTING_EVENT.name());
    collectionManagedAttributeDto.setCreatedBy("created by");
    collectionManagedAttributeDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    return collectionManagedAttributeDto;
  }
}
