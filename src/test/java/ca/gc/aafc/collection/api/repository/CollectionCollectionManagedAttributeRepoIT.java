package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionManagedAttributeTestFixture;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement.VocabularyElementType;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import javax.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectionCollectionManagedAttributeRepoIT extends CollectionModuleBaseRepositoryIT {

  private static final String BASE_URL = "/api/v1/" + CollectionManagedAttributeDto.TYPENAME;

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Inject
  private CollectionManagedAttributeRepo repo;

  @Autowired
  public CollectionCollectionManagedAttributeRepoIT(ObjectMapper objMapper) {
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
  @WithMockKeycloakUser(groupRole = "dina-group:SUPER_USER")
  void create_recordCreated() throws Exception {
    String expectedName = "dina attribute #12";
    String expectedValue = "dina value";
    String expectedCreatedBy = "dina";
    String expectedGroup = "dina-group";

    CollectionManagedAttributeDto dto = CollectionManagedAttributeTestFixture.newCollectionManagedAttribute();
    dto.setName(expectedName);
    dto.setVocabularyElementType(VocabularyElementType.INTEGER);
    dto.setAcceptedValues(new String[]{expectedValue});
    dto.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    dto.setCreatedBy(expectedCreatedBy);
    dto.setGroup(expectedGroup);

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      null, CollectionManagedAttributeDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(dto)
    );

    var created = repo.onCreate(docToCreate);
    UUID uuid = JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(created);

    var findOneResponse = sendGet(uuid.toString());
    JsonApiDocument apiDoc = toJsonApiDocument(findOneResponse);

     CollectionManagedAttributeDto findOneDto = objMapper.convertValue(apiDoc.getAttributes(),
       CollectionManagedAttributeDto.class);

    assertEquals(uuid, apiDoc.getId());
    assertEquals(expectedName, findOneDto.getName());
    assertEquals("dina_attribute_12", findOneDto.getKey());
    assertEquals(expectedValue, findOneDto.getAcceptedValues()[0]);
    Assertions.assertNotNull(findOneDto.getCreatedBy());
    assertEquals(expectedGroup, findOneDto.getGroup());
    assertEquals(VocabularyElementType.INTEGER, findOneDto.getVocabularyElementType());
    assertEquals(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT,
      findOneDto.getManagedAttributeComponent());
  }

  @Test
  @WithMockKeycloakUser(groupRole = CollectionManagedAttributeTestFixture.GROUP + ":SUPER_USER")
  void findOneByKey_whenKeyProvided_managedAttributeFetched() throws Exception {
    CollectionManagedAttributeDto newAttribute = CollectionManagedAttributeTestFixture.newCollectionManagedAttribute();
    newAttribute.setName("Collecting Event Attribute 1");
    newAttribute.setVocabularyElementType(VocabularyElementType.INTEGER);
    newAttribute.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      null, CollectionManagedAttributeDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(newAttribute)
    );

    var created = repo.onCreate(docToCreate);
    UUID newAttributeUuid = JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(created);

    var findOneResponse = sendGet("/collecting_event.collecting_event_attribute_1");
    JsonApiDocument apiDoc = objMapper.readValue(findOneResponse.getResponse().getContentAsString(),
      JsonApiDocument.class);

    assertEquals(newAttributeUuid, apiDoc.getId());
  }

  @Test
  @WithMockKeycloakUser(groupRole = CollectionManagedAttributeTestFixture.GROUP + ":SUPER_USER")
  void findOneByKey_whenBadKeyProvided_responseSanitized() throws Exception {
    ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,
      () -> repo.onFindOne("MATERIAL_SAMPLE.attr_1<iframe src=javascript:alert(24109)", null));

    assertFalse(exception.getMessage().contains("alert(24109)"));
  }
}
