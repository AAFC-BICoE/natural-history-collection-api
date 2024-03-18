package ca.gc.aafc.collection.api.rest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.dto.ResourceNameIdentifierResponseDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.specification.RequestSpecification;
import java.util.Properties;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Import(ResourceNameIdentifierRepositoryIT.ResourceNameIdentifierRepositoryTestConfiguration.class)
public class ResourceNameIdentifierRepositoryIT extends BaseRestAssuredTest {

  @LocalServerPort
  protected int testPort;

  protected ResourceNameIdentifierRepositoryIT() {
    super("/api/v1/");
  }

  @Test
  public void resourceNameIdentifierRepository_onPost_responseReturned() {

    CollectionDto collectionDto = newCollection();
    CollectionFixture.newCollection();
    collectionDto.setName("aaaeeee");

    String createdUUID = sendPost(CollectionDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
      CollectionDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(collectionDto),
      null,
      null)
    ).extract().body().jsonPath().getString("data.id");

    String uuid = newRequest().get("api/v1/" + ResourceNameIdentifierResponseDto.TYPE +
        "?filter[type][EQ]=collection&filter[name][EQ]=aaaeeee&filter[group][EQ]=aafc")
      .then().extract().body().jsonPath().getString("data.id");

    assertEquals(createdUUID, uuid);
  }

  private RequestSpecification newRequest() {
    return given()
      .config(RestAssured.config()
        .encoderConfig(EncoderConfig.encoderConfig()
          .defaultCharsetForContentType("UTF-8", JSON_API_CONTENT_TYPE)
          .defaultCharsetForContentType("UTF-8", JSON_PATCH_CONTENT_TYPE)))
      .port(testPort);
  }

  private static CollectionDto newCollection() {
    CollectionDto collectionDto = CollectionFixture.newCollection()
      .group("aafc")
      .build();
    collectionDto.setParentCollection(null);
    collectionDto.setInstitution(null);
    return collectionDto;
  }

  @TestConfiguration
  static class ResourceNameIdentifierRepositoryTestConfiguration {
    @Bean
    public BuildProperties buildProperties() {
      Properties props = new Properties();
      props.setProperty("version", "collection-module-version");
      return new BuildProperties(props);
    }
  }

}
