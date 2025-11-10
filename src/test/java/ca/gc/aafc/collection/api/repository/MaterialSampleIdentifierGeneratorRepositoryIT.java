package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleIdentifierGeneratorDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
import ca.gc.aafc.collection.api.entities.SplitConfiguration;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;

public class MaterialSampleIdentifierGeneratorRepositoryIT extends BaseRepositoryIT {

  @Inject
  private MaterialSampleIdentifierGeneratorRepository materialSampleIdentifierGeneratorRepository;

  @Inject
  protected MaterialSampleRepository materialSampleRepository;

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void materialSampleIdentifierGeneratorRepositoryNext_nextIdentifierReturned() {

    MaterialSampleDto parentDto = MaterialSampleTestFixture.newMaterialSample();
    parentDto.setMaterialSampleType(MaterialSample.MaterialSampleType.WHOLE_ORGANISM);
    parentDto.setMaterialSampleName("ABC-01");
    UUID parentUuid = createMaterialSample(parentDto);

    MaterialSampleDto child1 = MaterialSampleTestFixture.newMaterialSample();
    child1.setMaterialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN);
    child1.setMaterialSampleName("ABC-01-a");
    UUID child1Uuid = createMaterialSampleWithParent(child1, parentUuid);

    MaterialSampleIdentifierGeneratorDto generatedDto = MaterialSampleIdentifierGeneratorDto.builder()
      .currentParentUUID(child1Uuid)
      .strategy(MaterialSampleNameGeneration.IdentifierGenerationStrategy.TYPE_BASED)
      .characterType(MaterialSampleNameGeneration.CharacterType.LOWER_LETTER)
      .materialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN)
      .quantity(2)
      .build();

    MaterialSampleIdentifierGeneratorDto dto = materialSampleIdentifierGeneratorRepository.create(generatedDto);
    List<String> nextIdentifiers = dto.getNextIdentifiers().get(child1Uuid);
    assertEquals("ABC-01-b", nextIdentifiers.get(0));
    assertEquals("ABC-01-c", nextIdentifiers.get(1));
  }

  private UUID createMaterialSample(MaterialSampleDto dto) {
    JsonApiDocument materialSampleToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(dto)
    );
    return JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
      .onCreate(materialSampleToCreate));
  }

  private UUID createMaterialSampleWithParent(MaterialSampleDto dto, UUID parentUUID) {
    JsonApiDocument materialSampleToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(dto),
      Map.of("parentMaterialSample", JsonApiDocument.ResourceIdentifier.builder()
        .id(parentUUID).type(MaterialSampleDto.TYPENAME).build())
    );
    return JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
      .onCreate(materialSampleToCreate));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void materialSampleIdentifierGeneratorRepositorySameType_nextIdentifierReturned() {

    MaterialSampleDto parentDto = MaterialSampleTestFixture.newMaterialSample();
    parentDto.setMaterialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN);
    parentDto.setMaterialSampleName("Sample10");
    parentDto.setIsBaseForSplitByType(true);
    UUID parentUuid = createMaterialSample(parentDto);

    MaterialSampleIdentifierGeneratorDto generatedDto = MaterialSampleIdentifierGeneratorDto.builder()
      .currentParentUUID(parentUuid)
      .strategy(MaterialSampleNameGeneration.IdentifierGenerationStrategy.TYPE_BASED)
      .characterType(MaterialSampleNameGeneration.CharacterType.UPPER_LETTER)
      .materialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN)
      .separator(SplitConfiguration.Separator.DASH)
      .quantity(2)
      .build();

    MaterialSampleIdentifierGeneratorDto dto = materialSampleIdentifierGeneratorRepository.create(generatedDto);
    List<String> nextIdentifiers = dto.getNextIdentifiers().get(parentUuid);
    assertEquals("Sample10-A", nextIdentifiers.get(0));
    assertEquals("Sample10-B", nextIdentifiers.get(1));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void materialSampleIdentifierGeneratorRepositoryMultipleNext_nextIdentifierReturned() {

    MaterialSampleDto parentDto = MaterialSampleTestFixture.newMaterialSample();
    parentDto.setMaterialSampleType(MaterialSample.MaterialSampleType.WHOLE_ORGANISM);
    parentDto.setMaterialSampleName("ABC-01");
    UUID parentUuid = createMaterialSample(parentDto);

    MaterialSampleDto child1 = MaterialSampleTestFixture.newMaterialSample();
    child1.setMaterialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN);
    child1.setMaterialSampleName("ABC-01-a");
    UUID child1Uuid = createMaterialSampleWithParent(child1, parentUuid);

    MaterialSampleDto child2 = MaterialSampleTestFixture.newMaterialSample();
    child2.setMaterialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN);
    child2.setMaterialSampleName("ABC-01-b");
    UUID child2Uuid = createMaterialSampleWithParent(child2, parentUuid);

    MaterialSampleIdentifierGeneratorDto generatedDto = MaterialSampleIdentifierGeneratorDto.builder()
      .currentParentsUUID(List.of(child1Uuid, child2Uuid))
      .strategy(MaterialSampleNameGeneration.IdentifierGenerationStrategy.TYPE_BASED)
      .characterType(MaterialSampleNameGeneration.CharacterType.LOWER_LETTER)
      .materialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN)
      .build();

    MaterialSampleIdentifierGeneratorDto dto = materialSampleIdentifierGeneratorRepository.create(generatedDto);
    List<String> nextIdentifiers = dto.getNextIdentifiers().get(child1Uuid);
    assertEquals("ABC-01-c", nextIdentifiers.getFirst());

    nextIdentifiers = dto.getNextIdentifiers().get(child2Uuid);
    assertEquals("ABC-01-d", nextIdentifiers.getFirst());
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void materialSampleIdentifierGeneratorRepositoryNext_customSeparator() {

    MaterialSampleDto parentDto = MaterialSampleTestFixture.newMaterialSample();
    parentDto.setMaterialSampleType(MaterialSample.MaterialSampleType.WHOLE_ORGANISM);
    parentDto.setMaterialSampleName("ABC-01");
    parentDto.setIsBaseForSplitByType(true);
    UUID parentUuid = createMaterialSample(parentDto);

    MaterialSampleIdentifierGeneratorDto generatedDto = MaterialSampleIdentifierGeneratorDto.builder()
      .currentParentUUID(parentUuid)
      .strategy(MaterialSampleNameGeneration.IdentifierGenerationStrategy.TYPE_BASED)
      .characterType(MaterialSampleNameGeneration.CharacterType.LOWER_LETTER)
      .materialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN)
      .separator(SplitConfiguration.Separator.SPACE)
      .quantity(2)
      .build();

    MaterialSampleIdentifierGeneratorDto dto = materialSampleIdentifierGeneratorRepository.create(generatedDto);
    List<String> nextIdentifiers = dto.getNextIdentifiers().get(parentUuid);
    assertEquals("ABC-01 a", nextIdentifiers.get(0));
    assertEquals("ABC-01 b", nextIdentifiers.get(1));
  }

}
