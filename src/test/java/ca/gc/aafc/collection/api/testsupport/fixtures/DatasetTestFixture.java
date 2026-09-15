package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import ca.gc.aafc.collection.api.dto.DatasetDto;
import ca.gc.aafc.dina.dto.BaseDatasetDto.DatasetType;
import ca.gc.aafc.dina.dto.BaseDatasetDto.KeywordSet;
import ca.gc.aafc.dina.dto.BaseDatasetDto.Methods;
import ca.gc.aafc.dina.dto.BaseDatasetDto.Project;
import ca.gc.aafc.dina.dto.BaseDatasetDto.UsageRights;
import ca.gc.aafc.dina.dto.BaseDatasetDto.Coverage;
import ca.gc.aafc.dina.entity.AgentRoles;

public class DatasetTestFixture {

  private static final String GROUP = "aafc";

  public static DatasetDto newDataset() {
    DatasetDto datasetDto = new DatasetDto();
    datasetDto.setGroup(GROUP);
    datasetDto.setDatasetType(DatasetType.DWCA);
    datasetDto.setPublicationDate(LocalDate.now());
    datasetDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    datasetDto.setAgentRoles(List.of(AgentRoles.builder().agent(UUID.randomUUID()).roles(List.of("creator")).build()));
    datasetDto.setKeywordSets(List.of(KeywordSet.builder().keywords(List.of("a", "b")).thesaurus("alphabet").build()));
    datasetDto.setUsageRights(UsageRights.builder().licenseName("license name").build());
    datasetDto.setCoverage(Coverage.builder().build());
    datasetDto.setMethods(Methods.builder().build());
    datasetDto.setProject(Project.builder().build());

    return datasetDto;
  }

}
