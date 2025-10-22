package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.collection.api.service.ProjectService;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.factories.ProjectFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.ProjectTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import javax.inject.Inject;

public class ProjectRepositoryIT extends BaseRepositoryIT {
  
  @Inject 
  private ProjectRepository projectRepository;

  @Inject
  private ProjectService projectService;

  @Inject
  protected ServiceTransactionWrapper serviceTransactionWrapper;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void create_WithAuthenticatedUser_SetsCreatedBy()
      throws ResourceGoneException, ResourceNotFoundException {
    ProjectDto projectDto = ProjectTestFixture.newProject();
    UUID projectUUID = createWithRepository(projectDto, projectRepository::onCreate);

    ProjectDto result = projectRepository.getOne(projectUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertEquals(projectDto.getName(), result.getName());
    assertEquals(projectDto.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC:user"})
  public void updateFromDifferentGroup_throwAccessDenied()
      throws ResourceGoneException, ResourceNotFoundException {
    Project testProject = ProjectFactory.newProject()
      .group("preparation process definition")
      .name("aafc")
      .build();
    serviceTransactionWrapper.execute( projectService::create, testProject);

    ProjectDto retrievedProject = projectRepository.getOne(testProject.getUuid(), "").getDto();
    JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
      retrievedProject.getUuid(), CollectionManagedAttributeDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(retrievedProject)
    );

    assertThrows(AccessDeniedException.class, () -> projectRepository.onUpdate(docToUpdate, docToUpdate.getId()));
  }
}
