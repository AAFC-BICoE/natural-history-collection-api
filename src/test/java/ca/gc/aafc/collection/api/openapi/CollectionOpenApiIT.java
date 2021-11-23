package ca.gc.aafc.collection.api.openapi;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.InstitutionDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.InstitutionFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPIRelationship;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import lombok.SneakyThrows;
import org.apache.http.client.utils.URIBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class CollectionOpenApiIT extends BaseRestAssuredTest {

  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "DINA-Web/collection-specs/master/schema/natural-history-collection-api.yml";
  private static final URIBuilder URI_BUILDER = new URIBuilder();

  public static final String TYPE_NAME = "collection";

  static {
    URI_BUILDER.setScheme("https");
    URI_BUILDER.setHost(SPEC_HOST);
    URI_BUILDER.setPath(SPEC_PATH);
  }

  protected CollectionOpenApiIT() {
    super("/api/v1/");
  }

  public static URL getOpenAPISpecsURL() throws URISyntaxException, MalformedURLException {
    return URI_BUILDER.build().toURL();
  }

  @SneakyThrows
  @Test
  void collectingEvent_SpecValid() {
    CollectionDto collectionDto = CollectionFixture.newCollection()
      .institution(null)
      .build();

    String institutionId = sendPost(
      InstitutionDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        InstitutionDto.TYPENAME, JsonAPITestHelper.toAttributeMap(InstitutionFixture.newInstitution().build())
        , null, null)).extract().body().jsonPath().getString("data.id");

    String parentId = sendPost(
      TYPE_NAME, JsonAPITestHelper.toJsonAPIMap(
        TYPE_NAME, JsonAPITestHelper.toAttributeMap(CollectionFixture.newCollection().institution(null).build())
        , null, null)).extract().body().jsonPath().getString("data.id");

    OpenAPI3Assertions.assertRemoteSchema(getOpenAPISpecsURL(), "Collection",
      sendPost(
        TYPE_NAME,
        JsonAPITestHelper.toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(collectionDto),
          JsonAPITestHelper.toRelationshipMap(List.of(
            JsonAPIRelationship.of("institution", InstitutionDto.TYPENAME, institutionId),
            JsonAPIRelationship.of("parentCollection", TYPE_NAME, parentId))
          ),
          null)
      ).extract().asString());
  }

}
