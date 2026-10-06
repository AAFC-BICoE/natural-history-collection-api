package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;

import ca.gc.aafc.collection.api.dto.StorageUnitDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.StorageUnitTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
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

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  void create_WithAttachments_AttachmentsPersisted()
    throws ResourceGoneException, ResourceNotFoundException {
    StorageUnitDto storageUnitDto = StorageUnitTestFixture.newStorageUnit();

    UUID firstAttachmentUUID = UUID.randomUUID();
    UUID secondAttachmentUUID = UUID.randomUUID();

    JsonApiDocument storageUnitToCreate = JsonApiDocuments.createJsonApiDocument(
      null,
      StorageUnitDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(storageUnitDto),
      Map.of(
        "attachment",
        List.of(
          JsonApiDocument.ResourceIdentifier.builder()
            .id(firstAttachmentUUID)
            .type("metadata")
            .build(),
          JsonApiDocument.ResourceIdentifier.builder()
            .id(secondAttachmentUUID)
            .type("metadata")
            .build()
        )
      )
    );

    UUID storageUnitUUID =
      createWithRepository(storageUnitToCreate, storageUnitRepo::onCreate);

    StorageUnitDto result =
      storageUnitRepo.getOne(storageUnitUUID, "include=attachment").getDto();

    assertEquals(2, result.getAttachment().size());
    assertEquals(
      firstAttachmentUUID.toString(),
      result.getAttachment().get(0).getId()
    );
    assertEquals(
      secondAttachmentUUID.toString(),
      result.getAttachment().get(1).getId()
    );
  }
}
