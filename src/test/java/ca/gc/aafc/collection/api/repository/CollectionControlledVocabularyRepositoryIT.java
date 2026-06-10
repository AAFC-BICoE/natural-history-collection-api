package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.dto.CollectionControlledVocabularyDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionManagedAttributeTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.inject.Inject;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    var findOneResponse = sendGet("managed_attribute");
    JsonApiDocument apiDoc = objMapper.readValue(findOneResponse.getResponse().getContentAsString(),
      JsonApiDocument.class);
    assertEquals(CollectionVocabularyConfiguration.MANAGED_ATTRIBUTE_VOCAB_UUID, apiDoc.getId());

    // try by uuid
    sendGet(CollectionVocabularyConfiguration.MANAGED_ATTRIBUTE_VOCAB_UUID.toString());
  }

  @Test
  @WithMockKeycloakUser(groupRole = CollectionManagedAttributeTestFixture.GROUP + ":SUPER_USER")
  void filterByType_whenFiqlOrQueryProvided_returnsOk() throws Exception {
    mockMvc.perform(
        MockMvcRequestBuilders.get(BASE_URL)
          .queryParam("fiql", "type==MANAGED_ATTRIBUTE,type==SYSTEM")
          .contentType("application/vnd.api+json"))
      .andExpect(status().isOk());
  }

  @Test
  @WithMockKeycloakUser(groupRole = CollectionManagedAttributeTestFixture.GROUP + ":SUPER_USER")
  void filterByType_whenSimpleFilterWithEnumValuesProvided_correctCountReturned() {
    int managedAttributeCount = repo.getAll("filter[type][EQ]=MANAGED_ATTRIBUTE").totalCount();
    int systemCount = repo.getAll("filter[type][EQ]=SYSTEM").totalCount();

    assertTrue(managedAttributeCount > 0);
    assertTrue(systemCount > 0);

    assertEquals(managedAttributeCount + systemCount,
      repo.getAll("filter[type][EQ]=MANAGED_ATTRIBUTE,SYSTEM").totalCount());
  }
}
