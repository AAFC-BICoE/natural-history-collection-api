package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTypeTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;
import javax.inject.Inject;

class StorageUnitTypeRepoIT extends BaseRepositoryIT {

  @Inject
  private StorageUnitTypeRepo storageUnitTypeRepo;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  void find() throws ResourceGoneException, ResourceNotFoundException {

    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();
    UUID storageUnitTypeUUID = createWithRepository(storageUnitTypeDto, storageUnitTypeRepo::onCreate);

    StorageUnitTypeDto result = storageUnitTypeRepo.getOne(storageUnitTypeUUID, "").getDto();

    assertNotNull(result.getCreatedBy());
    assertNotNull(result.getCreatedOn());
    assertEquals(storageUnitTypeDto.getName(), result.getName());
    assertEquals(storageUnitTypeDto.getGroup(), result.getGroup());
  }
}
