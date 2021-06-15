package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.entities.PreparationType;
import ca.gc.aafc.collection.api.service.PreparationTypeService;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationTypeFactory;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;


@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class PreparationTypeRepositoryIT extends BaseRestAssuredTest {

  protected PreparationTypeRepositoryIT() {
    super("/api/v1/");
  }

  @Inject
  private PreparationTypeRepository preparationTypeRepository;

  @Inject
  private PreparationTypeService preparationTypeService;

  private static final String TYPE_NAME = "preparation-type";
  public static final String JSON_API_CONTENT_TYPE = "application/vnd.api+json";

  private static final String group = "aafc";
  private static final String name = "preparation process definition";

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc: staff"})
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    PreparationTypeDto pt = newPreparationTypeDto();
    PreparationTypeDto result = preparationTypeRepository.findOne(
      preparationTypeRepository.create(pt).getUuid(),
      new QuerySpec(PreparationTypeDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(pt.getName(), result.getName());
    assertEquals(pt.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC: staff"})
  public void updateFromDifferentGroup_throwAccessDenied() {
      PreparationType testPreparationType = PreparationTypeFactory.newPreparationType()
        .group(group)
        .name(name)
        .build();
      preparationTypeService.create(testPreparationType);
      PreparationTypeDto retrievedPreparationType = preparationTypeRepository.findOne(testPreparationType.getUuid(),
          new QuerySpec(PreparationTypeDto.class));
      assertThrows(AccessDeniedException.class, () -> preparationTypeRepository.save(retrievedPreparationType));
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

  private PreparationTypeDto newPreparationTypeDto() {
    PreparationTypeDto pt = new PreparationTypeDto();
    pt.setName(name);
    pt.setGroup(group);
    pt.setUuid(UUID.randomUUID());
    return pt;
  }
}
