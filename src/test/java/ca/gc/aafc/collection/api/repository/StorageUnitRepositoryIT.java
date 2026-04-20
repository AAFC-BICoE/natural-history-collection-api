package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import jakarta.inject.Inject;

public class StorageUnitRepositoryIT extends BaseRepositoryIT {

  @Inject
  private StorageUnitRepo storageUnitRepo;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  void create_findUsingOptFields() {

    StorageUnitDto storageUnitDto = StorageUnitTestFixture.newStorageUnit();
    UUID storageUnitUUID = createWithRepository(storageUnitDto, storageUnitRepo::onCreate);

    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.setQueryString(
      "include=storageUnitType&optfields[storage-unit]=storageUnitChildren");

    // make sure to test lazy loading
    var response = storageUnitRepo.onFindAll(mockRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
  }
}
