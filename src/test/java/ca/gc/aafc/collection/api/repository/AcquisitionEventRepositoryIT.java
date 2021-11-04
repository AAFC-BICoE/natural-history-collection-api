package ca.gc.aafc.collection.api.repository;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.AcquisitionEventDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.AcquisitionEventTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;

@SpringBootTest(properties = "keycloak.enabled=true")
public class AcquisitionEventRepositoryIT extends CollectionModuleBaseIT  {

  @Inject
  private AcquisitionEventRepository acquisitionEventRepository;
  
  @Test
  @WithMockKeycloakUser(groupRole = {"aafc: staff"})
  void find() {
    AcquisitionEventDto expected = acquisitionEventRepository.create(AcquisitionEventTestFixture.newAcquisitionEvent());
    AcquisitionEventDto result = acquisitionEventRepository.findOne(expected.getUuid(), new QuerySpec(AcquisitionEventDto.class));
    Assertions.assertNotNull(result.getCreatedBy());
    Assertions.assertNotNull(result.getCreatedOn());

    Assertions.assertEquals(expected.getExternallyIsolatedBy(), result.getExternallyIsolatedBy());
    Assertions.assertEquals(expected.getExternallyIsolatedOn(), result.getExternallyIsolatedOn());
    Assertions.assertEquals(expected.getExternallyIsolationRemarks(), result.getExternallyIsolationRemarks());

    Assertions.assertEquals(expected.getReceivedFrom(), result.getReceivedFrom());
    Assertions.assertEquals(expected.getReceivedDate(), result.getReceivedDate());
    Assertions.assertEquals(expected.getReceptionRemarks(), result.getReceptionRemarks());

  }
}
