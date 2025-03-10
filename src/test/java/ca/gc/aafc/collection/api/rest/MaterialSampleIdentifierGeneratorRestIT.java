package ca.gc.aafc.collection.api.rest;

import java.util.UUID;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleIdentifierGeneratorDto;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
        classes = CollectionModuleApiLauncher.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
public class MaterialSampleIdentifierGeneratorRestIT extends BaseRestAssuredTest {

  protected MaterialSampleIdentifierGeneratorRestIT() {
    super("/api/v1/");
  }

  @Test
  public void testPost() {
    MaterialSampleDto sampleDto = MaterialSampleTestFixture.newMaterialSample();
    sampleDto.setMaterialSampleName("TEST-POST-2");
    sampleDto.setAttachment(null);
    sampleDto.setPreparedBy(null);
    sampleDto.setPreparationProtocol(null);

    String matSampleUUID = sendPost(MaterialSampleDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        MaterialSampleDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(sampleDto),
        null,
        null)
      ).extract().body().jsonPath().getString("data.id");

    MaterialSampleIdentifierGeneratorDto dto = MaterialSampleIdentifierGeneratorDto
            .builder()
      .currentParentUUID(UUID.fromString(matSampleUUID))
      .strategy(MaterialSampleNameGeneration.IdentifierGenerationStrategy.DIRECT_PARENT)
      .quantity(1).build();
    assertEquals("TEST-POST-2-a", postMaterialSampleIdentifierGeneratorRest(dto));
  }

  private String postMaterialSampleIdentifierGeneratorRest(MaterialSampleIdentifierGeneratorDto dto) {
    return sendPost(
      MaterialSampleIdentifierGeneratorDto.TYPENAME,
      JsonAPITestHelper.toJsonAPIMap(MaterialSampleIdentifierGeneratorDto.TYPENAME, dto)
    ).extract().body().jsonPath().getString("data.attributes.nextIdentifiers." + dto.getCurrentParentUUID() + "[0]");
  }

}
