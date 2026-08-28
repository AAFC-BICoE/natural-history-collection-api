package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.dto.DatasetDto;
import ca.gc.aafc.dina.dto.DatasetDto.DatasetType;

public class DatasetTestFixture {

  private static final String GROUP = "aafc";

  public static DatasetDto newDataset() {
    DatasetDto datasetDto = new DatasetDto();
    datasetDto.setGroup(GROUP);
    datasetDto.setDatasetType(DatasetType.DWCA);
    datasetDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());

    return datasetDto;
  }

}
