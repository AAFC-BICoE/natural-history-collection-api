package ca.gc.aafc.collection.api.service;

import java.util.Map;
import javax.validation.ValidationException;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.testsupport.factories.ProjectFactory;

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
