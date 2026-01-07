package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
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
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaterialSampleIdentifierGeneratorRepositoryIT extends CollectionModuleBaseRepositoryIT {

  private static final String BASE_URL = "/api/v1/" + MaterialSampleIdentifierGeneratorDto.TYPENAME;

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Inject
  private MaterialSampleIdentifierGeneratorRepository materialSampleIdentifierGeneratorRepository;

  @Inject
  protected MaterialSampleRepository materialSampleRepository;

  @Autowired
  public MaterialSampleIdentifierGeneratorRepositoryIT(ObjectMapper objMapper) {
    super(BASE_URL, objMapper);
  }

  @Override
  protected MockMvc getMockMvc() {
    return mockMvc;
  }

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void materialSampleIdentifierGeneratorRepositoryNext_nextIdentifierReturned()
    throws Exception {

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

    MaterialSampleIdentifierGeneratorDto resultDto = postAndReturnResult(generatedDto);

    List<String> nextIdentifiers = resultDto.getNextIdentifiers().get(child1Uuid);
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

  private MaterialSampleIdentifierGeneratorDto postAndReturnResult(MaterialSampleIdentifierGeneratorDto toPost)
    throws Exception {
    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      null, CollectionManagedAttributeDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(toPost)
    );

    var createdResponse = sendPost(docToCreate);
    JsonApiDocument apiDoc = objMapper.readValue(createdResponse.getResponse().getContentAsString(),
      JsonApiDocument.class);

    return objMapper.convertValue(apiDoc.getAttributes(),
      MaterialSampleIdentifierGeneratorDto.class);
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void materialSampleIdentifierGeneratorRepositorySameType_nextIdentifierReturned()
    throws Exception {

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

    MaterialSampleIdentifierGeneratorDto resultDto = postAndReturnResult(generatedDto);

    List<String> nextIdentifiers = resultDto.getNextIdentifiers().get(parentUuid);
    assertEquals("Sample10-A", nextIdentifiers.get(0));
    assertEquals("Sample10-B", nextIdentifiers.get(1));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void materialSampleIdentifierGeneratorRepositoryMultipleNext_nextIdentifierReturned()
    throws Exception {

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

    MaterialSampleIdentifierGeneratorDto resultDto = postAndReturnResult(generatedDto);

    List<String> nextIdentifiers = resultDto.getNextIdentifiers().get(child1Uuid);
    assertEquals("ABC-01-c", nextIdentifiers.getFirst());

    nextIdentifiers = resultDto.getNextIdentifiers().get(child2Uuid);
    assertEquals("ABC-01-d", nextIdentifiers.getFirst());
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void materialSampleIdentifierGeneratorRepositoryNext_customSeparator() throws Exception {

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

    MaterialSampleIdentifierGeneratorDto resultDto = postAndReturnResult(generatedDto);

    List<String> nextIdentifiers = resultDto.getNextIdentifiers().get(parentUuid);
    assertEquals("ABC-01 a", nextIdentifiers.get(0));
    assertEquals("ABC-01 b", nextIdentifiers.get(1));
  }
}
