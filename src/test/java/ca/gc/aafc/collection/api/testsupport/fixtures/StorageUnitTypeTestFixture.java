package ca.gc.aafc.collection.api.testsupport.fixtures;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;

public class StorageUnitTypeTestFixture {
  
  private static final String GROUP = "aafc";

  public static StorageUnitTypeDto newStorageUnitType() {
    StorageUnitTypeDto storageUnitTypeDto = new StorageUnitTypeDto();
    storageUnitTypeDto.setName(RandomStringUtils.randomAlphabetic(5));
    storageUnitTypeDto.setGroup(GROUP);
    storageUnitTypeDto.setIsInseperable(false);
    storageUnitTypeDto.setGridLayoutDefinition(StorageGridLayoutTestFixture.newStorageGridLayout());
    return storageUnitTypeDto;
  }

}
