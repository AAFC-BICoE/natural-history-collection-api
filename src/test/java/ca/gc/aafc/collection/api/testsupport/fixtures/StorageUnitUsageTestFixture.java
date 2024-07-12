package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;

public class StorageUnitUsageTestFixture {

  public static StorageUnitUsageDto newStorageUnitUsage(StorageUnitDto storageUnit) {
    StorageUnitUsageDto storageUnitCoordinatesDto = new StorageUnitUsageDto();
    storageUnitCoordinatesDto.setWellColumn(1);
    storageUnitCoordinatesDto.setWellRow("A");
    storageUnitCoordinatesDto.setCreatedBy("test user");

    storageUnitCoordinatesDto.setStorageUnit(storageUnit);

    return storageUnitCoordinatesDto;
  }

}
