package ca.gc.aafc.collection.api.rest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.SiteTestFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Map;
import javax.inject.Inject;
import org.springframework.http.HttpStatus;

@SpringBootTest(classes = CollectionModuleApiLauncher.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "dev-user.enabled=true")
@TestPropertySource(properties = { "spring.config.additional-location=classpath:application-test.yml" })
@ContextConfiguration(initializers = { PostgresTestContainerInitializer.class })
@Import(CollectionModuleBaseIT.CollectionModuleTestConfiguration.class)
public class SiteRestIT extends BaseRestAssuredTest {

  @Inject
  private ObjectMapper om;

  protected SiteRestIT() {
    super("/api/v1/");
  }

  @Test
  void site_onCreate_serializationSuccessful() throws JsonProcessingException {
    SiteDto siteDto = SiteTestFixture.newSite();

    var attributes = JsonAPITestHelper.toAttributeMap(siteDto);
    attributes.put("siteGeom", SiteTestFixture.polygonGeoJson());

    // Make sure serialization is working on POST
    String uuid = JsonAPITestHelper.extractId(
        sendPost(SiteDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
            SiteDto.TYPENAME, attributes, null)));

    // Make sure serialization is working on GET
    var response = sendGet(SiteDto.TYPENAME, uuid).extract().body();
    SiteDto receivedDto = om.readValue(response.asString(), SiteDto.class);

    // Make sure serialization is working on PATCH
    Map<String, Object> attributesToUpdate = Map.of("siteGeom", SiteTestFixture.polygonGeoJson());
    var updateResponse = sendPatch(SiteDto.TYPENAME, uuid,
        JsonAPITestHelper.toJsonAPIMap(SiteDto.TYPENAME, attributesToUpdate, uuid), 200);
    assertEquals(siteDto.getSiteGeom(), receivedDto.getSiteGeom());
    assertNotNull(updateResponse);
    assertEquals(HttpStatus.OK.value(), updateResponse.extract().statusCode());
  }
}
