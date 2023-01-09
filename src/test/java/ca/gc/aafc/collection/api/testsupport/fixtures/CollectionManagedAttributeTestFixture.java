package ca.gc.aafc.collection.api.testsupport.fixtures;

import org.apache.commons.lang3.RandomStringUtils;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement.VocabularyElementType;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;

public class CollectionManagedAttributeTestFixture {

  public static final String GROUP = "dina";

  public static CollectionManagedAttributeDto newCollectionManagedAttribute() {
    CollectionManagedAttributeDto collectionManagedAttributeDto = new CollectionManagedAttributeDto();
    collectionManagedAttributeDto.setName(RandomStringUtils.randomAlphabetic(5));
    collectionManagedAttributeDto.setGroup(GROUP);
    collectionManagedAttributeDto.setManagedAttributeType(VocabularyElementType.INTEGER);
    collectionManagedAttributeDto.setAcceptedValues(new String[]{"value"});
    collectionManagedAttributeDto.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    collectionManagedAttributeDto.setCreatedBy("created by");   
    collectionManagedAttributeDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    return collectionManagedAttributeDto;
  }



}
