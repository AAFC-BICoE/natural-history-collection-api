package ca.gc.aafc.collection.api.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.testsupport.factories.ProjectFactory;
import ca.gc.aafc.dina.entity.AgentRoles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProjectServiceIT extends CollectionModuleBaseIT {

  @Test
  void project_OnCreate_projectCreated() {
    Project project = ProjectFactory.newProject()
      .extensionValues(Map.of("ncbi_sra_project_v1", Map.of("submitted_to_sra", "2024-09-04")))
      .build();

    projectService.create(project);

    Project projectReloaded = projectService.findOne(project.getUuid(), Project.class);

    assertEquals(project.getName(), projectReloaded.getName());
  }

  @Test
  void project_onInvalidProjectRole_exception() {
    Project project = ProjectFactory.newProject()
      .contributors(List.of(AgentRoles.builder().agent(UUID.randomUUID()).roles(List.of("abc")).build()))
      .build();
    assertThrows(ValidationException.class, () -> projectService.create(project));
  }

  @Test
  void project_onValidProjectRole_NoException() {
    Project project = ProjectFactory.newProject()
      .contributors(List.of(AgentRoles.builder().agent(UUID.randomUUID()).roles(List.of("data_curator")).build()))
      .build();
    projectService.create(project);
  }

  @Test
  void project_onInvalidValue_exception() {
    Project project = ProjectFactory.newProject()
      .extensionValues(Map.of("ncbi_sra_project_v1", Map.of("submitted_to_sra", "a")))
      .build();
    assertThrows(ValidationException.class, () -> projectService.create(project));
  }

  @Test
  void project_onInvalidExtension_exception() {
    Project project = ProjectFactory.newProject()
      .extensionValues(Map.of("mixs_soil_v5", Map.of("crop_rotation", "a")))
      .build();
    assertThrows(ValidationException.class, () -> projectService.create(project));
  }
}
