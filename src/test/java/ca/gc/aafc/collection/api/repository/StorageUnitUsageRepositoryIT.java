package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.dto.StorageUnitTypeDto;
import ca.gc.aafc.collection.api.dto.StorageUnitUsageDto;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTypeTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitUsageTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;
import javax.validation.ValidationException;

public class StorageUnitUsageRepositoryIT extends BaseRepositoryIT {

  @Inject
  private StorageUnitTypeRepo storageUnitTypeRepo;

  @Inject
  private StorageUnitRepo storageUnitRepo;

  @Inject
  private StorageUnitUsageRepository storageUnitUsageRepository;

  @Inject
  protected ServiceTransactionWrapper serviceTransactionWrapper;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitCoordinatesRepository_onCreateWithValidData_created()
      throws ResourceGoneException, ResourceNotFoundException {

    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();
    UUID storageUnitTypeUUID = createWithRepository(storageUnitTypeDto, storageUnitTypeRepo::onCreate);

    StorageUnitDto storageUnitDto = StorageUnitTestFixture.newStorageUnit();
    UUID storageUnitUUID = createStorageUnit(storageUnitDto, storageUnitTypeUUID);

    StorageUnitUsageDto dto = StorageUnitUsageTestFixture.newStorageUnitUsage(storageUnitDto);
    UUID storageUnitUsageUUID = createStorageUnitUsage(dto, storageUnitUUID);

    // reload
    StorageUnitUsageDto reloadedDto = serviceTransactionWrapper.executeWithParam( (a) -> {
      try {
        return storageUnitUsageRepository.getOne(a, "").getDto();
      } catch (ResourceNotFoundException | ResourceGoneException e) {
        throw new RuntimeException(e);
      }
    }, storageUnitUsageUUID);
    assertEquals(1, reloadedDto.getCellNumber());
    assertEquals(storageUnitDto.getName(), reloadedDto.getStorageUnitName());
  }

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitCoordinatesRepository_onCreateWithInvalidData_exception() {

    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();
    UUID storageUnitTypeUUID = createWithRepository(storageUnitTypeDto, storageUnitTypeRepo::onCreate);
    UUID storageUnitUUID = createStorageUnit(StorageUnitTestFixture.newStorageUnit(), storageUnitTypeUUID);

    StorageUnitUsageDto dto = StorageUnitUsageTestFixture.newStorageUnitUsage(null);
    dto.setWellColumn(200);

    assertThrows(ValidationException.class, ()-> createStorageUnitUsage(dto, storageUnitUUID));
  }

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitUsageRepository_onCreateLargeStorage_NoException() {

    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();
    storageUnitTypeDto.getGridLayoutDefinition().setNumberOfColumns(10);
    storageUnitTypeDto.getGridLayoutDefinition().setNumberOfRows(50);
    UUID storageUnitTypeUUID = createWithRepository(storageUnitTypeDto, storageUnitTypeRepo::onCreate);
    UUID storageUnitUUID = createStorageUnit(StorageUnitTestFixture.newStorageUnit(), storageUnitTypeUUID);


    StorageUnitUsageDto dto = StorageUnitUsageTestFixture.newStorageUnitUsage(null);
    dto.setWellRow("AH");
    dto.setWellColumn(1);

    assertNotNull(createStorageUnitUsage(dto, storageUnitUUID));
  }

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitUsageRepository_onCreateWithoutStorage_exception() {

    StorageUnitUsageDto dto = StorageUnitUsageTestFixture.newStorageUnitUsage(null);
    // access denied since the group is loaded from the storage
    assertThrows(AccessDeniedException.class, ()-> createStorageUnitUsage(dto, null));
  }

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void storageUnitUsageRepository_onCreateWithoutCoordinates() {

    StorageUnitTypeDto storageUnitTypeDto = StorageUnitTypeTestFixture.newStorageUnitType();
    storageUnitTypeDto.setGridLayoutDefinition(null);
    UUID storageUnitTypeUUID = createWithRepository(storageUnitTypeDto, storageUnitTypeRepo::onCreate);
    UUID storageUnitUUID = createStorageUnit(StorageUnitTestFixture.newStorageUnit(), storageUnitTypeUUID);

    StorageUnitUsageDto dto = StorageUnitUsageTestFixture.newStorageUnitUsage(null);
    dto.setWellColumn(null);
    dto.setWellRow(null);

    createStorageUnitUsage(dto, storageUnitUUID);
  }

  private UUID createStorageUnit(StorageUnitDto storageUnitDto, UUID storageUnitTypeUUID) {
    JsonApiDocument storageUnitToCreate = JsonApiDocuments.createJsonApiDocument(
      null, StorageUnitDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(storageUnitDto),
      Map.of("storageUnitType", JsonApiDocument.ResourceIdentifier.builder()
        .id(storageUnitTypeUUID).type(StorageUnitTypeDto.TYPENAME).build())
    );
    return JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(storageUnitRepo
      .onCreate(storageUnitToCreate));
  }

  private UUID createStorageUnitUsage(StorageUnitUsageDto dto, UUID storageUnitUUID) {
    JsonApiDocument storageUnitUsageToCreate = JsonApiDocuments.createJsonApiDocument(
      null, StorageUnitUsageDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(dto),
      Map.of("storageUnit", JsonApiDocument.ResourceIdentifier.builder()
        .id(storageUnitUUID).type(StorageUnitDto.TYPENAME).build())
    );
    return JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(storageUnitUsageRepository
      .onCreate(storageUnitUsageToCreate));
  }
}
