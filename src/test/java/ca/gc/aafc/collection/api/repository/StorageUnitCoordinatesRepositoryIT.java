package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.StorageUnitCoordinatesDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitCoordinatesTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTypeTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import javax.inject.Inject;
import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "keycloak.enabled = true")
public class StorageUnitCoordinatesRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private StorageUnitTypeRepo storageUnitTypeRepo;

  @Inject
  private StorageUnitRepo storageUnitRepo;

  @Inject
  private StorageUnitCoordinatesRepository storageUnitCoordinatesRepository;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitCoordinatesRepository_onCreateWithValidData_created() {

    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();
    storageUnitTypeDto = storageUnitTypeRepo.create(storageUnitTypeDto);

    StorageUnitDto storageUnitDto = StorageUnitTestFixture.newStorageUnit();
    storageUnitDto.setStorageUnitType(storageUnitTypeDto);
    storageUnitDto = storageUnitRepo.create(storageUnitDto);

    StorageUnitCoordinatesDto dto = StorageUnitCoordinatesTestFixture.newStorageUnitCoordinates(storageUnitDto);
    storageUnitCoordinatesRepository.create(dto);

  }

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitCoordinatesRepository_onCreateWithInvalidData_exception() {

    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();
    storageUnitTypeDto = storageUnitTypeRepo.create(storageUnitTypeDto);

    StorageUnitDto storageUnitDto = StorageUnitTestFixture.newStorageUnit();
    storageUnitDto.setStorageUnitType(storageUnitTypeDto);
    storageUnitDto = storageUnitRepo.create(storageUnitDto);

    StorageUnitCoordinatesDto dto = StorageUnitCoordinatesTestFixture.newStorageUnitCoordinates(storageUnitDto);
    dto.setWellColumn(200);
    assertThrows(ValidationException.class, ()-> storageUnitCoordinatesRepository.create(dto));
  }

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitCoordinatesRepository_onCreateWithoutStorage_exception() {

    StorageUnitCoordinatesDto dto = StorageUnitCoordinatesTestFixture.newStorageUnitCoordinates(null);
    // access denied since they group is loaded from the storage
    assertThrows(AccessDeniedException.class, ()-> storageUnitCoordinatesRepository.create(dto));
  }
  
}
