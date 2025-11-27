package ca.gc.aafc.collection.api.testsupport.fixtures;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyItemDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.entity.ControlledVocabularyItem;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement.VocabularyElementType;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;

public class CollectionManagedAttributeTestFixture {

  public static final String GROUP = "dina";

  public static CollectionManagedAttributeDto newCollectionManagedAttribute() {
    CollectionManagedAttributeDto collectionManagedAttributeDto = new CollectionManagedAttributeDto();
    collectionManagedAttributeDto.setName(RandomStringUtils.randomAlphabetic(5));
    collectionManagedAttributeDto.setGroup(GROUP);
    collectionManagedAttributeDto.setVocabularyElementType(VocabularyElementType.INTEGER);
    collectionManagedAttributeDto.setAcceptedValues(new String[]{"1", "2"});
    collectionManagedAttributeDto.setUnit("cm");
    collectionManagedAttributeDto.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    collectionManagedAttributeDto.setCreatedBy("created by");   
    collectionManagedAttributeDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    return collectionManagedAttributeDto;
  }

  public static CollectionControlledVocabularyItemDto newCollectionManagedAttribute2() {
    CollectionControlledVocabularyItemDto collectionManagedAttributeDto = new CollectionControlledVocabularyItemDto();
    collectionManagedAttributeDto.setName(RandomStringUtils.randomAlphabetic(5));
    collectionManagedAttributeDto.setGroup(GROUP);
    collectionManagedAttributeDto.setVocabularyElementType(VocabularyElementType.INTEGER);
    collectionManagedAttributeDto.setAcceptedValues(new String[]{"1", "2"});
    collectionManagedAttributeDto.setUnit("cm");
    collectionManagedAttributeDto.setDinaComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT.name());
    collectionManagedAttributeDto.setCreatedBy("created by");
    collectionManagedAttributeDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    return collectionManagedAttributeDto;
  }

}
