package ca.gc.aafc.collection.api.repository;

import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.MaterialSampleCounterDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

@SpringBootTest(properties = "keycloak.enabled=true")
public class MaterialSampleCounterRepositoryIT extends CollectionModuleBaseIT {

  private static final String VALID_GROUP = "aafc";

  @Inject
  private MaterialSampleCounterRepository materialSampleCounterRepository;

  @Test
  @WithMockKeycloakUser(groupRole = VALID_GROUP + ":user")
  public void reserveNewSequenceIds_accessGranted_idReserved() {

    MaterialSample ms = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.create(ms);

    MaterialSampleCounterDto msCounterDto = new MaterialSampleCounterDto();
    msCounterDto.setMaterialSampleUUID(ms.getUuid());
    msCounterDto.setCounterName(MaterialSample.MaterialSampleType.CULTURE_STRAIN.name());
    msCounterDto.setAmount(2);

    // Send the request to the repository.
    MaterialSampleCounterDto response = materialSampleCounterRepository.create(msCounterDto);
    Assertions.assertEquals(1, response.getResult().lowNumber());
    Assertions.assertEquals(2, response.getResult().highNumber());

    // try again tu make sure we can continue the series
    response = materialSampleCounterRepository.create(msCounterDto);
    Assertions.assertEquals(3, response.getResult().lowNumber());
    Assertions.assertEquals(4, response.getResult().highNumber());
  }

}
