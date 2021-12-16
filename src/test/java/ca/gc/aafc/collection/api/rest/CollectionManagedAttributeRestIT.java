package ca.gc.aafc.collection.api.rest;

import java.util.HashMap;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.shaded.org.apache.commons.lang.RandomStringUtils;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import io.restassured.response.ValidatableResponse;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class CollectionManagedAttributeRestIT extends BaseRestAssuredTest {

  public static final String API_BASE_PATH_MANAGEDATTRIBUTE = "/api/v1/managed-attribute/";
  
  protected CollectionManagedAttributeRestIT() {
    super("/api/v1/");
  }

  @Test
  void post_managedAttribute_noExceptionThrown() {

    sendPost(
      "managed-attribute", 
      JsonAPITestHelper.toJsonAPIMap(
        "managed-attribute",
        new HashMap<String, Object>() {{
          put("name", RandomStringUtils.randomAlphabetic(5));
          put("group", "dina");
          put("managedAttributeType", "STRING");
          put("createdBy", "created By");
          put("multilingualDescription", "{description: []}");
      }},
        null,
        null
      )
    );
  }
}
