package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleKeycloakBaseIT;
import ca.gc.aafc.collection.api.dto.AssemblageDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.AssemblageTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AssemblageRepositoryIT extends CollectionModuleKeycloakBaseIT {

  @Inject
  private AssemblageRepository assemblageRepository;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    AssemblageDto assemblage = AssemblageTestFixture.newAssemblage();
    AssemblageDto result = assemblageRepository.findOne(
            assemblageRepository.create(assemblage).getUuid(),
            new QuerySpec(AssemblageDto.class));

    assertNotNull(result.getCreatedBy());
    assertEquals(assemblage.getName(), result.getName());
    assertEquals(assemblage.getGroup(), result.getGroup());
  }

}
