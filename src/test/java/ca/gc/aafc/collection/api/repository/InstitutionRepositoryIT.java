package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.InstitutionDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.InstitutionFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;

@SpringBootTest(
  properties = "keycloak.enabled = true"
)
class InstitutionRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private InstitutionRepository institutionRepository;

  @Test
  @WithMockKeycloakUser(username = "dev", adminRole = {"DINA_ADMIN"})
  void find() {
    InstitutionDto expected = institutionRepository.create(InstitutionFixture.newInstitution().build());
    InstitutionDto result = institutionRepository.findOne(expected.getUuid(), new QuerySpec(InstitutionDto.class));
    Assertions.assertNotNull(result.getCreatedBy());
    Assertions.assertNotNull(result.getCreatedOn());
    Assertions.assertEquals(expected.getName(), result.getName());
    Assertions.assertEquals(
      expected.getMultilingualDescription().getDescriptions().getFirst().getLang(),
      result.getMultilingualDescription().getDescriptions().getFirst().getLang());
  }

}
