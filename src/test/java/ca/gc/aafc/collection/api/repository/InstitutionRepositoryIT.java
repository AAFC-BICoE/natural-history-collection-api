package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.InstitutionDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.InstitutionFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import javax.inject.Inject;

class InstitutionRepositoryIT extends BaseRepositoryIT {

  @Inject
  private InstitutionRepository institutionRepository;

  @Test
  @WithMockKeycloakUser(username = "dev", adminRole = {"DINA_ADMIN"})
  void find() throws ResourceGoneException, ResourceNotFoundException {

    InstitutionDto institution = InstitutionFixture.newInstitution().build();
    UUID collMethodUUID = createWithRepository(institution, institutionRepository::onCreate);
    InstitutionDto result = institutionRepository.getOne(collMethodUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertNotNull(result.getCreatedOn());
    assertEquals(institution.getName(), result.getName());
    assertEquals(
      institution.getMultilingualDescription().getDescriptions().getFirst().getLang(),
      result.getMultilingualDescription().getDescriptions().getFirst().getLang());
  }

}
