package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.AssemblageTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import javax.inject.Inject;

public class AssemblageRepositoryIT extends BaseRepositoryIT {

  @Inject
  private AssemblageRepository assemblageRepository;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void create_WithAuthenticatedUser_SetsCreatedBy()
      throws ResourceGoneException, ResourceNotFoundException {

    AssemblageDto assemblage = AssemblageTestFixture.newAssemblage();
    UUID assemblageUUID = createWithRepository(assemblage, assemblageRepository::onCreate);
    AssemblageDto result = assemblageRepository.getOne(assemblageUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertEquals(assemblage.getName(), result.getName());
    assertEquals(assemblage.getGroup(), result.getGroup());
  }

}
