package ca.gc.aafc.collection.api.repository;

import java.util.UUID;
import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyDto;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionManagedAttributeTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectionControlledVocabularyRepositoryIT extends CollectionModuleBaseRepositoryIT {

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

    var findOneResponse = getMockMvc().perform(
        get(baseUrl)
          .contentType(BaseRestAssuredTest.JSON_API_CONTENT_TYPE)
      )
      .andExpect(status().isOk())
      .andReturn();
  }
}
