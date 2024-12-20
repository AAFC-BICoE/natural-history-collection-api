package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.entity.AgentRoles;

public class ProjectTestFixture {

  private static final String GROUP = "aafc";
  private static final LocalDate START_DATE = LocalDate.of(1991, 01, 01);
  private static final LocalDate END_DATE = LocalDate.now();

  public static ProjectDto newProject() {
    ProjectDto projectDto = new ProjectDto();
    projectDto.setName(RandomStringUtils.randomAlphabetic(5));
    projectDto.setStatus(RandomStringUtils.randomAlphabetic(5));
    projectDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    projectDto.setStartDate(START_DATE);
    projectDto.setEndDate(END_DATE);
    projectDto.setGroup(GROUP);
    projectDto.setContributors(List.of(
      AgentRoles.builder().agent(UUID.randomUUID()).roles(List.of("project_leader")).build()
    ));

    projectDto.setAttachment(List.of(
      ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("file").build()));
    return projectDto;
  }
  
}
