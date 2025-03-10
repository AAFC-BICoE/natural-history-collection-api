package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.entities.StorageUnit;
import ca.gc.aafc.collection.api.entities.StorageUnitType;
import ca.gc.aafc.collection.api.entities.StorageUnitUsage;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitFactory;
import ca.gc.aafc.collection.api.testsupport.factories.StorageUnitTypeFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitUsageTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTypeTestFixture;
import ca.gc.aafc.dina.entity.StorageGridLayout;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.dina.validation.ValidationErrorsHelper;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(properties = "keycloak.enabled = true")
public class StorageUnitUsageRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private StorageUnitTypeRepo storageUnitTypeRepo;

  @Inject
  private StorageUnitRepo storageUnitRepo;

  @Inject
  private StorageUnitUsageRepository storageUnitUsageRepository;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitCoordinatesRepository_onCreateWithValidData_created() {

    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();
    storageUnitTypeDto = storageUnitTypeRepo.create(storageUnitTypeDto);

    StorageUnitDto storageUnitDto = StorageUnitTestFixture.newStorageUnit();
    storageUnitDto.setStorageUnitType(storageUnitTypeDto);
    storageUnitDto = storageUnitRepo.create(storageUnitDto);

    StorageUnitUsageDto dto = StorageUnitUsageTestFixture.newStorageUnitUsage(storageUnitDto);

    StorageUnitUsageDto stored = storageUnitUsageRepository.create(dto);
    assertEquals(1, stored.getCellNumber());
    assertEquals(storageUnitDto.getName(), stored.getStorageUnitName());
  }

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitCoordinatesRepository_onCreateWithInvalidData_exception() {

    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();
    storageUnitTypeDto = storageUnitTypeRepo.create(storageUnitTypeDto);

    StorageUnitDto storageUnitDto = StorageUnitTestFixture.newStorageUnit();
    storageUnitDto.setStorageUnitType(storageUnitTypeDto);
    storageUnitDto = storageUnitRepo.create(storageUnitDto);

    StorageUnitUsageDto dto = StorageUnitUsageTestFixture.newStorageUnitUsage(storageUnitDto);
    dto.setWellColumn(200);
    assertThrows(ValidationException.class, ()-> storageUnitUsageRepository.create(dto));
  }

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitUsageRepository_onCreateLargeStorage_NoException() {

    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();
    storageUnitTypeDto.getGridLayoutDefinition().setNumberOfColumns(10);
    storageUnitTypeDto.getGridLayoutDefinition().setNumberOfRows(50);
    storageUnitTypeDto = storageUnitTypeRepo.create(storageUnitTypeDto);

    StorageUnitDto storageUnitDto = StorageUnitTestFixture.newStorageUnit();
    storageUnitDto.setStorageUnitType(storageUnitTypeDto);
    storageUnitDto = storageUnitRepo.create(storageUnitDto);

    StorageUnitUsageDto dto = StorageUnitUsageTestFixture.newStorageUnitUsage(storageUnitDto);
    dto.setWellRow("AH");
    dto.setWellColumn(1);

    storageUnitUsageRepository.create(dto);
  }

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitUsageRepository_onCreateWithoutStorage_exception() {

    StorageUnitUsageDto dto = StorageUnitUsageTestFixture.newStorageUnitUsage(null);
    // access denied since they group is loaded from the storage
    assertThrows(AccessDeniedException.class, ()-> storageUnitUsageRepository.create(dto));
  }

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitUsageRepository_onCreateWithoutCoordinates() {

    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();
    storageUnitTypeDto.setGridLayoutDefinition(null);
    storageUnitTypeDto = storageUnitTypeRepo.create(storageUnitTypeDto);

    StorageUnitDto storageUnitDto = StorageUnitTestFixture.newStorageUnit();
    storageUnitDto.setStorageUnitType(storageUnitTypeDto);
    storageUnitDto = storageUnitRepo.create(storageUnitDto);

    StorageUnitUsageDto dto = StorageUnitUsageTestFixture.newStorageUnitUsage(storageUnitDto);
    dto.setWellColumn(null);
    dto.setWellRow(null);

    storageUnitUsageRepository.create(dto);
  }
}
