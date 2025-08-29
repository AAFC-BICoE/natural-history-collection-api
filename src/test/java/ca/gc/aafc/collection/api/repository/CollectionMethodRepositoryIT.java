package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.CollectionMethodDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionMethodTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import javax.inject.Inject;

class CollectionMethodRepositoryIT extends BaseRepositoryIT {

  @Inject
  private CollectionMethodRepository repository;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:super-user"})
  void create_WithAuthUser_CreatedBySet() throws ResourceGoneException, ResourceNotFoundException {
    CollectionMethodDto expected = CollectionMethodTestFixture.newMethod();
    UUID collMethodUUID = createWithRepository(expected, repository::onCreate);

    CollectionMethodDto result = repository.getOne(collMethodUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertEquals(expected.getName(), result.getName());
    assertEquals(expected.getGroup(), result.getGroup());
    assertEquals("dev", result.getCreatedBy());
    assertEquals(
      expected.getMultilingualDescription().getDescriptions().getFirst().getLang(),
      result.getMultilingualDescription().getDescriptions().getFirst().getLang());
  }
}
