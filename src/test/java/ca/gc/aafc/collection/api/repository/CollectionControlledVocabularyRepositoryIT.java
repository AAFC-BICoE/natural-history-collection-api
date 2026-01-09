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
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionManagedAttributeTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import javax.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectionControlledVocabularyRepositoryIT extends CollectionModuleBaseRepositoryIT {

  public static final UUID MANAGED_ATTRIBUTE_UUID = UUID.fromString("01998155-a6f0-7c2f-9fcc-994d74222f9c");

  private static final String BASE_URL = "/api/v1/" + CollectionControlledVocabularyDto.TYPENAME;

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Inject
  private CollectionControlledVocabularyRepository repo;

  @Autowired
  public CollectionControlledVocabularyRepositoryIT(ObjectMapper objMapper) {
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
    var findOneResponse = sendGet("managed_attribute");
    JsonApiDocument apiDoc = objMapper.readValue(findOneResponse.getResponse().getContentAsString(),
      JsonApiDocument.class);
    assertEquals(MANAGED_ATTRIBUTE_UUID, apiDoc.getId());

    // try by uuid
    sendGet(MANAGED_ATTRIBUTE_UUID.toString());
  }
}
