package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.ProjectFactory;
import ca.gc.aafc.dina.i18n.MultilingualDescription;

public class ProjectCRUDIT extends CollectionModuleBaseIT {

  private static final String EXPECTED_NAME = "name";
  private static final String EXPECTED_GROUP = "DINA GROUP";
  private static final String EXPECTED_CREATED_BY = "createdBy";
  private static final String EXPECTED_STATUS= "READY";
  private static final LocalDate EXPECTED_START_DATE = LocalDate.of(1991, 01, 01);
  private static final LocalDate EXPECTED_END_DATE = LocalDate.now();
  private final List<UUID> EXPECT_ATTACHMENTS = List.of(UUID.randomUUID(), UUID.randomUUID());

  private static final MultilingualDescription.MultilingualPair MULTILINGUAL_PAIR_FR = MultilingualDescription.MultilingualPair.builder()
    .desc("description en français")
    .lang("fr")
    .build();

  private static final MultilingualDescription.MultilingualPair MULTILINGUAL_PAIR_EN = MultilingualDescription.MultilingualPair.builder()
    .desc("description in english")
    .lang("en")
    .build();

  private static final MultilingualDescription MULTILINGUAL_DESCRIPTION = MultilingualDescription.builder()
    .descriptions(List.of(MULTILINGUAL_PAIR_EN, MULTILINGUAL_PAIR_FR))
    .build();

  @Test
  void create() {
    Project project = buildExpectedProject();

    projectService.create(project);
    
    Assertions.assertNotNull(project.getId());
    Assertions.assertNotNull(project.getCreatedOn());
    Assertions.assertNotNull(project.getUuid());
  }

  @Test
  void find() {
    Project project = buildExpectedProject();

    projectService.create(project);

    Project result = projectService.findOne(
      project.getUuid(),
      Project.class);
    Assertions.assertEquals(EXPECTED_NAME, result.getName());
    Assertions.assertEquals(EXPECTED_GROUP, result.getGroup());
    Assertions.assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
    Assertions.assertEquals(EXPECTED_STATUS, result.getStatus());
    Assertions.assertEquals(EXPECTED_START_DATE, result.getStartDate());
    Assertions.assertEquals(EXPECTED_END_DATE, result.getEndDate());
    Assertions.assertEquals(EXPECT_ATTACHMENTS, result.getAttachment());
    Assertions.assertEquals(MULTILINGUAL_DESCRIPTION.getDescriptions(), result.getMultilingualDescription().getDescriptions());
  
  }

  private Project buildExpectedProject() {
    return ProjectFactory.newProject()
      .name(EXPECTED_NAME)
      .group(EXPECTED_GROUP)
      .createdBy(EXPECTED_CREATED_BY)
      .multilingualDescription(MULTILINGUAL_DESCRIPTION)
      .status(EXPECTED_STATUS)
      .startDate(EXPECTED_START_DATE)
      .endDate(EXPECTED_END_DATE)
      .attachment(EXPECT_ATTACHMENTS)
      .build();
  }
  
}
