package ca.gc.aafc.collection.api.rest;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.ImmutableMaterialSampleDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.repository.StorageUnitRepo;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@Transactional
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class MaterialSampleRestIT extends BaseRestAssuredTest {

  protected MaterialSampleRestIT() {
    super("/api/v1/");
  }

  @Test
  void post_withChild_childIgnored() {
    ImmutableMaterialSampleDto childDto = new ImmutableMaterialSampleDto();
    childDto.setUuid(UUID.fromString(postSample(newSample())));

    MaterialSampleDto parent = newSample();
    parent.setMaterialSampleChildren(List.of(childDto));

    String parentId = postSample(parent);

    findSample(parentId).body("data.attributes.materialSampleChildren", Matchers.empty());
  }

  @Test
  void patch_withChild_childIgnored() {
    ImmutableMaterialSampleDto childDto = new ImmutableMaterialSampleDto();
    childDto.setUuid(UUID.fromString(postSample(newSample())));

    MaterialSampleDto parent = newSample();
    String parentId = postSample(parent);

    parent.setMaterialSampleChildren(List.of(childDto));

    sendPatch(
      MaterialSampleDto.TYPENAME, parentId,
      JsonAPITestHelper.toJsonAPIMap(
        MaterialSampleDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(parent),
        null,
        null)
    );

    findSample(parentId).body("data.attributes.materialSampleChildren", Matchers.empty());
  }

  private MaterialSampleDto newSample() {
    MaterialSampleDto sampleDto = MaterialSampleTestFixture.newMaterialSample();
    sampleDto.setAttachment(null);
    sampleDto.setPreparedBy(null);
    return sampleDto;
  }

  private String postSample(MaterialSampleDto parent) {
    return sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
      MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(parent),
      null,
      null)
    ).extract().body().jsonPath().getString("data.id");
  }

  private ValidatableResponse findSample(String unitId) {
    return RestAssured.given().header(CRNK_HEADER).port(this.testPort).basePath(this.basePath)
      .get(MaterialSampleDto.TYPENAME + "/" + unitId + "?include=materialSampleChildren"
        + StorageUnitRepo.HIERARCHY_INCLUDE_PARAM).then();
  }

}
