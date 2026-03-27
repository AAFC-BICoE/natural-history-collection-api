package ca.gc.aafc.collection.api.repository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.dina.dto.ApiInfoDto;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static com.toedter.spring.hateoas.jsonapi.MediaTypes.JSON_API_VALUE;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.inject.Inject;


public class ApiInfoRepositoryIT extends BaseRepositoryIT {

  @Inject
  private ObjectMapper objectMapper;

  @Value("${dina.apiPrefix:}")
  private String apiPrefix;

  @Inject
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  @WithMockKeycloakUser(adminRole = "DINA_ADMIN")
  public void onApiInfo_infoReturned() throws Exception {
    var response = mockMvc.perform(
        get(apiPrefix + "/" + ApiInfoDto.TYPE_NAME)
          .contentType(JSON_API_VALUE))
      .andExpect(status().isOk())
      .andReturn();

    JsonNode jsonApiDoc = objectMapper.readValue(response.getResponse().getContentAsString(), JsonNode.class);
    ApiInfoDto dto = objectMapper.convertValue(jsonApiDoc.get("data").get("attributes"), ApiInfoDto.class);
    assertNotNull(dto);
  }
}
