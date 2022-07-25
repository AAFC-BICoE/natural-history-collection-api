package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.dina.dto.ExternalRelationDto;

public class ProjectTestFixture {

  private static final String GROUP = "aafc";
  private static final LocalDate START_DATE = LocalDate.of(1991, 01, 01);
  private static final LocalDate END_DATE = LocalDate.now();

  public static ProjectDto newProject() {
    ProjectDto preparationTypeDto = new ProjectDto();
    preparationTypeDto.setName(RandomStringUtils.randomAlphabetic(5));
    preparationTypeDto.setStatus(RandomStringUtils.randomAlphabetic(5));
    preparationTypeDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    preparationTypeDto.setStartDate(START_DATE);
    preparationTypeDto.setEndDate(END_DATE);
    preparationTypeDto.setGroup(GROUP);
    preparationTypeDto.setAttachment(List.of(
      ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("file").build()));
    return preparationTypeDto;
  }
  
}
