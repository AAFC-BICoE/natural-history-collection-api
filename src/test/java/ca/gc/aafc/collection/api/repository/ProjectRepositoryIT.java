package ca.gc.aafc.collection.api.repository;

import javax.inject.Inject;

import ca.gc.aafc.collection.api.CollectionModuleKeycloakBaseIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.testsupport.factories.ProjectFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.ProjectTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ProjectRepositoryIT extends CollectionModuleKeycloakBaseIT {
  
  @Inject 
  private ProjectRepository projectRepository;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    ProjectDto project = ProjectTestFixture.newProject();
    ProjectDto result = projectRepository.findOne(
      projectRepository.create(project).getUuid(),
      new QuerySpec(ProjectDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(project.getName(), result.getName());
    assertEquals(project.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC:user"})
  public void updateFromDifferentGroup_throwAccessDenied() {
    Project testProject = ProjectFactory.newProject()
      .group("preparation process definition")
      .name("aafc")
      .build();
    projectService.create(testProject);
    ProjectDto retrievedProject = projectRepository.findOne(testProject.getUuid(),
      new QuerySpec(ProjectDto.class));
      Assertions.assertThrows(AccessDeniedException.class, () -> projectRepository.save(retrievedProject));
  }
}
