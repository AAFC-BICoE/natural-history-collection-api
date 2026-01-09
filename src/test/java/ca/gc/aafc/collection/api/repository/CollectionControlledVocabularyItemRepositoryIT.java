package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyDto;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyItemDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionControlledVocabularyItemTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionManagedAttributeTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectionControlledVocabularyItemRepositoryIT extends CollectionModuleBaseRepositoryIT {

  private static final String BASE_URL = "/api/v1/" + CollectionControlledVocabularyItemDto.TYPENAME;

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Inject
  private CollectionControlledVocabularyItemRepository repo;

  @Autowired
  public CollectionControlledVocabularyItemRepositoryIT(ObjectMapper objMapper) {
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
  @WithMockKeycloakUser(groupRole = CollectionManagedAttributeTestFixture.GROUP + ":SUPER_USER")
  void findOneByKey_whenKeyProvided_managedAttributeFetched() throws Exception {

  }

  @Test
  @WithMockKeycloakUser(groupRole = "dina-group:DINA_ADMIN", adminRole = "DINA_ADMIN")
  void create_recordCreated() throws Exception {
    String expectedName = "dina attribute #12";
    String expectedValue = "dina value";
    String expectedCreatedBy = "dina";
    String expectedGroup = "dina-group";

    CollectionControlledVocabularyItemDto dto =
      CollectionControlledVocabularyItemTestFixture.newCollectionControlledVocabularyItem();

    dto.setName(expectedName);
    dto.setVocabularyElementType(TypedVocabularyElement.VocabularyElementType.INTEGER);
    dto.setAcceptedValues(new String[]{expectedValue});
    dto.setDinaComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT.name());
    dto.setCreatedBy(expectedCreatedBy);
    dto.setGroup(expectedGroup);

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocumentWithRelToOne(
      null, CollectionControlledVocabularyItemDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(dto),
      Map.of("controlledVocabulary", JsonApiDocument.ResourceIdentifier.builder()
        .type(CollectionControlledVocabularyDto.TYPENAME)
        .id(CollectionControlledVocabularyRepositoryIT.MANAGED_ATTRIBUTE_UUID).build()
      )
    );

    var created = repo.onCreate(docToCreate);
    UUID uuid = JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(created);

    // try get by uuid
    sendGet(uuid.toString());

    // try get be key
    sendGet("managed_attribute.dina_attribute_12.COLLECTING_EVENT");
  }
}
