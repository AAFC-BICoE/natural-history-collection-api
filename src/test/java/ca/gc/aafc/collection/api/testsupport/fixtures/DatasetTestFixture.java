package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.util.List;
import java.util.UUID;

import ca.gc.aafc.collection.api.dto.DatasetDto;
import ca.gc.aafc.dina.dto.DatasetDto.DatasetType;
import ca.gc.aafc.dina.dto.DatasetDto.KeywordSet;
import ca.gc.aafc.dina.dto.DatasetDto.UsageRights;
import ca.gc.aafc.dina.dto.DatasetDto.Coverage;
import ca.gc.aafc.dina.entity.AgentRoles;

public class DatasetTestFixture {

  private static final String GROUP = "aafc";

  public static DatasetDto newDataset() {
    DatasetDto datasetDto = new DatasetDto();
    datasetDto.setGroup(GROUP);
    datasetDto.setDatasetType(DatasetType.DWCA);
    datasetDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    datasetDto.setAgentRoles(List.of(AgentRoles.builder().agent(UUID.randomUUID()).roles(List.of("creator")).build()));
    datasetDto.setKeywordSets(List.of(new KeywordSet(List.of("a", "b"), "alphabet")));
    datasetDto.setUsageRights(new UsageRights("license name", "", ""));
    datasetDto.setCoverage(new Coverage(null, null, null));

    return datasetDto;
  }

}
