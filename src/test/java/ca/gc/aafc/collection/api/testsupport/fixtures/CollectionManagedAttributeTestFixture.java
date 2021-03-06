package ca.gc.aafc.collection.api.testsupport.fixtures;

import org.apache.commons.lang3.RandomStringUtils;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;

public class CollectionManagedAttributeTestFixture {

  public static CollectionManagedAttributeDto newCollectionManagedAttribute() {
    CollectionManagedAttributeDto collectionManagedAttributeDto = new CollectionManagedAttributeDto();
    collectionManagedAttributeDto.setName(RandomStringUtils.randomAlphabetic(5));
    collectionManagedAttributeDto.setGroup("dina");
    collectionManagedAttributeDto.setManagedAttributeType(CollectionManagedAttribute.ManagedAttributeType.INTEGER);
    collectionManagedAttributeDto.setAcceptedValues(new String[]{"value"});
    collectionManagedAttributeDto.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    collectionManagedAttributeDto.setCreatedBy("created by");       
    return collectionManagedAttributeDto;
  }



}
