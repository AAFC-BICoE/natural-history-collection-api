package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.dina.entity.StorageGridLayout;

public class StorageGridLayoutTestFixture {

  public static StorageGridLayout newStorageGridLayout() {
    return StorageGridLayout.builder()
            .numberOfColumns(10)
            .numberOfRows(10)
            .fillDirection(StorageGridLayout.FillDirection.BY_ROW)
            .build();
  }
}
