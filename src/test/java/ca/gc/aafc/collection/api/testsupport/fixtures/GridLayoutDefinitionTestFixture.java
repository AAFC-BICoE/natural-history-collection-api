package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.entities.GridLayoutDefinition;

public class GridLayoutDefinitionTestFixture {

  public static GridLayoutDefinition newGridStorageDefinition() {
    return GridLayoutDefinition.builder()
            .numberOfColumns(10)
            .numberOfRows(10)
            .fillDirection(GridLayoutDefinition.FillDirection.BY_ROW)
            .build();
  }
}
