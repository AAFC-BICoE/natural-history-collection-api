package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import ca.gc.aafc.collection.api.dto.AssociationDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.AssociationTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.inject.Inject;
import java.util.Map;
import java.util.UUID;

public class AssociationRepositoryIT extends BaseRepositoryIT {

  @Inject
  private MaterialSampleRepository materialSampleRepository;

  @Inject
  private AssociationRepository associationRepository;

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  @Transactional
  void createDeleteAssociation() throws ResourceGoneException, ResourceNotFoundException {

    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
    materialSampleDto.setUuid(createWithRepository(materialSampleDto, materialSampleRepository));

    MaterialSampleDto materialSample2Dto = MaterialSampleTestFixture.newMaterialSample();
    materialSample2Dto.setUuid(createWithRepository(materialSampleDto, materialSampleRepository));

    JsonApiDocument associationToCreate = JsonApiDocuments.createJsonApiDocument(
      null, AssociationDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(AssociationTestFixture.newAssociation()),
      Map.of("sample", resourceIdentifierFromDto(materialSampleDto),
        "associatedSample", resourceIdentifierFromDto(materialSample2Dto))
    );

    UUID associationUUID = createWithRepository(associationToCreate, associationRepository::onCreate);
    AssociationDto result = associationRepository.getOne(associationUUID, "").getDto();
    assertNotNull(result);


    associationRepository.onDelete(result.getJsonApiId());
    assertThrows(ResourceGoneException.class, () -> associationRepository.getOne(associationUUID, ""));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  @Transactional
  void updateAssociation() throws ResourceGoneException, ResourceNotFoundException {

    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
    materialSampleDto.setUuid(createWithRepository(materialSampleDto, materialSampleRepository));

    MaterialSampleDto materialSample2Dto = MaterialSampleTestFixture.newMaterialSample();
    materialSample2Dto.setUuid(createWithRepository(materialSample2Dto, materialSampleRepository));

    JsonApiDocument associationToCreate = JsonApiDocuments.createJsonApiDocument(
      null, AssociationDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(AssociationTestFixture.newAssociation()),
      Map.of("sample", resourceIdentifierFromDto(materialSampleDto),
        "associatedSample", resourceIdentifierFromDto(materialSample2Dto))
    );

    UUID associationUUID = createWithRepository(associationToCreate, associationRepository::onCreate);
    AssociationDto result = associationRepository.getOne(associationUUID, "").getDto();
    assertNotNull(result);

    MaterialSampleDto materialSample3Dto = MaterialSampleTestFixture.newMaterialSample();
    materialSample3Dto.setUuid(createWithRepository(materialSample3Dto, materialSampleRepository));

    JsonApiDocument associationToUpdate = JsonApiDocuments.createJsonApiDocument(
      associationUUID, AssociationDto.TYPENAME,
      null,
      Map.of("associatedSample", resourceIdentifierFromDto(materialSample3Dto))
    );

    associationRepository.update(associationToUpdate);
    AssociationDto resultAfterUpdate = associationRepository.getOne(associationUUID, "include=associatedSample,sample").getDto();
    assertEquals(materialSample3Dto.getUuid(), resultAfterUpdate.getAssociatedSample().getUuid());
  }


//  @Test
//  void patch_ChangAssociationType() {
//    String associatedWithId = postSample(newSample());
//
//    MaterialSampleDto sample = newSample();
//    sample.setAssociations(List.of(AssociationDto.builder()
//      .associationType("host_of")
//      .associatedSample(UUID.fromString(associatedWithId))
//      .build()));
//    String sampleID = postSample(sample);
//
//    String ExpectedType = "parasite_of";
//    sample.setAssociations(List.of(AssociationDto.builder()
//      .associationType(ExpectedType)
//      .associatedSample(UUID.fromString(associatedWithId))
//      .build()));
//
//    sendPatch(sample, sampleID);
//    findSample(sampleID)
//      .body("data.attributes.associations", Matchers.hasSize(1))
//      .body("data.attributes.associations[0].associatedSample", Matchers.is(associatedWithId))
//      .body("data.attributes.associations[0].associationType", Matchers.is(ExpectedType));
//  }

}
