package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import org.apache.commons.lang3.RandomStringUtils;

public class StorageUnitTestFixture {

  private static final String GROUP = "aafc";

  public static StorageUnitDto newStorageUnit() {
    StorageUnitDto unitDto = new StorageUnitDto();
    unitDto.setName(RandomStringUtils.randomAlphabetic(8));
    unitDto.setGroup(GROUP);
    unitDto.setCreatedBy("test user");
    unitDto.setIsGeneric(false);
    return unitDto;
  }
}
