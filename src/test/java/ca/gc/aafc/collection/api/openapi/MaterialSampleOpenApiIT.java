package ca.gc.aafc.collection.api.openapi;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.PreparationTypeDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.entities.Determination;
import ca.gc.aafc.collection.api.repository.StorageUnitRepo;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.PreparationTypeTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import ca.gc.aafc.dina.testsupport.specs.ValidationRestrictionOptions;
import io.restassured.RestAssured;
import io.restassured.response.ResponseBody;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.UUID;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})

public class MaterialSampleOpenApiIT extends BaseRestAssuredTest {

  private static final String SPEC_HOST = "raw.githubusercontent.com";
  private static final String SPEC_PATH = "DINA-Web/collection-specs/master/schema/natural-history-collection-api.yml";
  private static final URIBuilder URI_BUILDER = new URIBuilder();

  public static final String TYPE_NAME = "material-sample";

  static {
    URI_BUILDER.setScheme("https");
    URI_BUILDER.setHost(SPEC_HOST);
    URI_BUILDER.setPath(SPEC_PATH);
  }

  protected MaterialSampleOpenApiIT() {
    super("/api/v1/");
  }

  public static URL getOpenAPISpecsURL() throws URISyntaxException, MalformedURLException {
    return URI_BUILDER.build().toURL();
  }

  @SneakyThrows
  @Test
  void collectingEvent_SpecValid() {
    CollectionManagedAttributeDto collectionManagedAttributeDto = new CollectionManagedAttributeDto();
    collectionManagedAttributeDto.setName("name");
    collectionManagedAttributeDto.setGroup("group");
    collectionManagedAttributeDto.setManagedAttributeType(CollectionManagedAttribute.ManagedAttributeType.STRING);
    collectionManagedAttributeDto.setAcceptedValues(null);
    collectionManagedAttributeDto.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.MATERIAL_SAMPLE);
    collectionManagedAttributeDto.setCreatedBy("dina");     

    sendPost("managed-attribute", JsonAPITestHelper.toJsonAPIMap("managed-attribute", JsonAPITestHelper.toAttributeMap(collectionManagedAttributeDto)));

    Determination determination = Determination.builder()
      .verbatimScientificName("verbatimScientificName")
      .verbatimDeterminer("verbatimDeterminer")
      .verbatimDate("2021-01-01")
      .scientificName("scientificName")
      .transcriberRemarks("transcriberRemarks")
      .typeStatus("typeStatus")
      .typeStatusEvidence("typeStatusEvidence")
      .determiner(List.of(UUID.randomUUID()))
      .determinedOn(LocalDate.now())
      .qualifier("qualifier")
      .scientificNameSource(Determination.ScientificNameSource.COLPLUS)
      .scientificNameDetails("scientificNameDetails")
      .build();

    MaterialSampleDto ms = MaterialSampleTestFixture.newMaterialSample();
    ms.setAttachment(null);
    ms.setPreparedBy(null);
    ms.setManagedAttributes(Map.of("name", "anything"));
    ms.setDetermination(List.of(determination));

    MaterialSampleDto parent = MaterialSampleTestFixture.newMaterialSample();
    parent.setDwcCatalogNumber("parent" + MaterialSampleTestFixture.DWC_CATALOG_NUMBER);
    parent.setMaterialSampleName("parent" + MaterialSampleTestFixture.MATERIAL_SAMPLE_NAME);
    parent.setAttachment(null);
    parent.setParentMaterialSample(null);
    parent.setMaterialSampleChildren(null);
    parent.setPreparedBy(null);


    MaterialSampleDto child = MaterialSampleTestFixture.newMaterialSample();
    child.setDwcCatalogNumber("child" + MaterialSampleTestFixture.DWC_CATALOG_NUMBER);
    child.setMaterialSampleName("child" + MaterialSampleTestFixture.MATERIAL_SAMPLE_NAME); 
    child.setAttachment(null);
    child.setParentMaterialSample(null);
    child.setMaterialSampleChildren(null);
    child.setPreparedBy(null);

    PreparationTypeDto preparationTypeDto = PreparationTypeTestFixture.newPreparationType();  
    preparationTypeDto.setCreatedBy("test user");  
    
    sendPost("material-sample", JsonAPITestHelper.toJsonAPIMap("material-sample", JsonAPITestHelper.toAttributeMap(parent)));
    sendPost("material-sample", JsonAPITestHelper.toJsonAPIMap("material-sample", JsonAPITestHelper.toAttributeMap(child)));
    
    ResponseBody materialSampleResponseBody = sendGet("material-sample", "").extract().response().body();

    String parentUUID = materialSampleResponseBody.path("data[0].id");
    String childUUID = materialSampleResponseBody.path("data[1].id");
    String preparationTypeUUID = sendPost("preparation-type", JsonAPITestHelper.toJsonAPIMap("preparation-type", JsonAPITestHelper.toAttributeMap(preparationTypeDto))).extract().response().body().path("data.id");

    String unitId = sendPost(
      TYPE_NAME, 
      JsonAPITestHelper.toJsonAPIMap(
        TYPE_NAME, 
        JsonAPITestHelper.toAttributeMap(ms),
        Map.of(
          "attachment", getRelationListType("metadata", UUID.randomUUID().toString()),
          "parentMaterialSample", getRelationType("material-sample", parentUUID),
          "preparedBy", getRelationType("person", UUID.randomUUID().toString()),
          "preparationType", getRelationType("preparation-type", preparationTypeUUID),
          "materialSampleChildren", getRelationListType("material-sample", childUUID)),
          null
        )
      ).extract().body().jsonPath().getString("data.id");
    
    OpenAPI3Assertions.assertRemoteSchema(
      getOpenAPISpecsURL(), 
      "MaterialSample", 
      RestAssured.given().header(CRNK_HEADER).port(this.testPort).basePath(this.basePath)
      .get(TYPE_NAME + "/" + unitId + "?include=attachment,preparedBy,preparationType,parentMaterialSample,materialSampleChildren,"
        + StorageUnitRepo.HIERARCHY_INCLUDE_PARAM).print(),
      ValidationRestrictionOptions.builder().allowAdditionalFields(false).allowableMissingFields(Set.of("collectingEvent")).build()
      );
    }

  private Map<String, Object> getRelationType(String type, String uuid) {
    return Map.of("data", Map.of(
      "id", uuid,
      "type", type));
  }

  private Map<String, Object> getRelationListType(String type, String uuid) {

    return Map.of("data", List.of(Map.of(
      "id", uuid,
      "type", type)));
  }
}
