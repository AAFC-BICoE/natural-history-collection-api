package ca.gc.aafc.collection.api.openapi;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.PreparationMethodDto;
import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.PreparationMethodTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.PreparationTypeTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.config.EncoderConfig;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;

import static io.restassured.config.DecoderConfig.decoderConfig;
import static io.restassured.config.EncoderConfig.encoderConfig;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class PreparationMethodOpenApiIT extends BaseRestAssuredTest {

  public static final String TYPE_NAME = "preparation-method";

  protected PreparationMethodOpenApiIT() {
    super("/api/v1/");
  }

  @SneakyThrows
  @Test
  void preparationMethod_SpecValid() {

    RestAssured.config().encoderConfig(encoderConfig()
                    .defaultContentCharset("UTF-8")
            .defaultCharsetForContentType("UTF-8", JSON_API_CONTENT_TYPE));

    RestAssured.config().decoderConfig(decoderConfig().defaultContentCharset("UTF-8"));

    PreparationMethodDto preparationMethodDto = PreparationMethodTestFixture.newPreparationMethod();
    preparationMethodDto.setCreatedBy("test user");

    OpenAPI3Assertions
        .assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, "PreparationMethod",
                sendPost(TYPE_NAME, JsonAPITestHelper
                .toJsonAPIMap(TYPE_NAME, JsonAPITestHelper.toAttributeMap(preparationMethodDto)))
                .extract().asString());
  }


}
