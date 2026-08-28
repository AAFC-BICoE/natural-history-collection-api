package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.DatasetDto;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.fixtures.DatasetTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import jakarta.inject.Inject;

public class DatasetRepositoryIT extends BaseRepositoryIT {
  
  @Inject 
  private DatasetRepository datasetRepository;

  @Inject
  protected ServiceTransactionWrapper serviceTransactionWrapper;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void create_WithAuthenticatedUser_SetsCreatedBy()
      throws ResourceGoneException, ResourceNotFoundException {
    DatasetDto datasetDto = DatasetTestFixture.newDataset();
    UUID projectUUID = createWithRepository(datasetDto, datasetRepository::onCreate);

    DatasetDto result = datasetRepository.getOne(projectUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertEquals(datasetDto.getGroup(), result.getGroup());
    assertEquals(datasetDto.getDatasetType(), result.getDatasetType());
  }
}
