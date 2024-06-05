package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.StorageUnitCoordinatesDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;

public class StorageUnitCoordinatesTestFixture {

  public static StorageUnitCoordinatesDto newStorageUnitCoordinates(StorageUnitDto storageUnit) {
    StorageUnitCoordinatesDto storageUnitCoordinatesDto = new StorageUnitCoordinatesDto();
    storageUnitCoordinatesDto.setWellColumn(1);
    storageUnitCoordinatesDto.setWellRow("A");
    storageUnitCoordinatesDto.setCreatedBy("test user");

    storageUnitCoordinatesDto.setStorageUnit(storageUnit);

    return storageUnitCoordinatesDto;
  }

}
