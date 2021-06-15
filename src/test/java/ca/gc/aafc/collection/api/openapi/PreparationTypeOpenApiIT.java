package ca.gc.aafc.collection.api.openapi;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class PreparationTypeOpenApiIT extends BaseRestAssuredTest {

  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "DINA-Web/collection-specs/master/schema/natural-history-collection-api.yml";
  private static final URIBuilder URI_BUILDER = new URIBuilder();

  public static final String TYPE_NAME = "preparation-type";

  private static final String name = "water";

  static {
    URI_BUILDER.setScheme("https");
    URI_BUILDER.setHost(SPEC_HOST);
    URI_BUILDER.setPath(SPEC_PATH);
  }

  protected PreparationTypeOpenApiIT() {
    super("/api/v1/");
  }

  public static URL getOpenAPISpecsURL() throws URISyntaxException, MalformedURLException {
    return URI_BUILDER.build().toURL();
  }

  @Test
  void preparationType_filterByGroupWithOperator() {
    PreparationTypeDto preparationTypeDto = new PreparationTypeDto();
    preparationTypeDto.setCreatedBy("test user");
    preparationTypeDto.setGroup("aafc");
    preparationTypeDto.setName(name);  
    String uuid = sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(preparationTypeDto))).extract().response().body().path("data[0].id");

    PreparationTypeDto preparationTypeDto_differentGroup = new PreparationTypeDto();
    preparationTypeDto_differentGroup.setCreatedBy("nottest user");
    preparationTypeDto_differentGroup.setGroup("NOTaafc");
    preparationTypeDto_differentGroup.setName("NOT" + name);  
    sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(preparationTypeDto_differentGroup)));

    String actualUuid = sendGet(TYPE_NAME+"?filter[group][eq]=aafc", "").extract().response().body().path("data[0].id");

    assertEquals(uuid, actualUuid);
  }

  @Test
  //TODO: Replace preparationType_filterByGroupWithOperator when this tests returns OK code 200
  void preparationType_filterByGroupWithoutOperator_BadRequest() {
    PreparationTypeDto preparationTypeDto = new PreparationTypeDto();
    preparationTypeDto.setCreatedBy("test user");
    preparationTypeDto.setGroup("aafc");
    preparationTypeDto.setName(name);  

    PreparationTypeDto preparationTypeDto_differentGroup = new PreparationTypeDto();
    preparationTypeDto_differentGroup.setCreatedBy("nottest user");
    preparationTypeDto_differentGroup.setGroup("NOTaafc");
    preparationTypeDto_differentGroup.setName("NOT" + name);  
    sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(preparationTypeDto_differentGroup)));

    sendGet(TYPE_NAME+"?filter[group]=aafc", "", HttpStatus.BAD_REQUEST.value());

  }

  @SneakyThrows
  @Test
  void preparationType_SpecValid() {
    PreparationTypeDto preparationTypeDto = new PreparationTypeDto();
    preparationTypeDto.setCreatedBy("test user");
    preparationTypeDto.setGroup("aafc");
    preparationTypeDto.setName(name);      

    OpenAPI3Assertions.assertRemoteSchema(getOpenAPISpecsURL(), "PreparationType",
      sendPost(TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(preparationTypeDto),
        null,
        null)
      ).extract().asString(), false);
  }

}
