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

import javax.inject.Inject;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(
  properties = {"spring.config.additional-location=classpath:application-test.yml"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
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
        SiteDto.TYPENAME, attributes, null)
      ));

    // Make sure serialization is working on GET
    var response = sendGet(SiteDto.TYPENAME, uuid).extract().body();
    SiteDto receivedDto = om.readValue(response.asString(), SiteDto.class);

    assertEquals(siteDto.getSiteGeom(), receivedDto.getSiteGeom());
  }
}
