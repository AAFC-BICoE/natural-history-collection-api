package ca.gc.aafc.collection.api.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class PreparationTypeJsonApiIT extends BaseRestAssuredTest {

  private static final String TYPE_NAME = "preparation-type";
  private static final String group = "aafc";
  private static final String name = "preparation process definition";

  protected PreparationTypeJsonApiIT() {
    super("/api/v1/");
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
  
}
