package ca.gc.aafc.collection.api.rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectingEventTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionManagedAttributeTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.ProtocolTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiBulkResourceIdentifierDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPIRelationship;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
  properties = "dev-user.enabled=true"
)
@TestPropertySource(properties = {"spring.config.additional-location=classpath:application-test.yml"})
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Import(CollectionModuleBaseIT.CollectionModuleTestConfiguration.class)
public class CollectingEventRestIT extends BaseRestAssuredTest {

  protected CollectingEventRestIT() {
    super("/api/v1/");
  }

  @Test
  void extension_onUpdate_updatePersisted() {

    ProtocolDto protocol = ProtocolTestFixture.newProtocol();
    protocol.setAttachments(null);
    String protocolUuid = postProtocol(protocol);

    CollectionManagedAttributeDto ceMa = CollectionManagedAttributeTestFixture.newCollectionManagedAttribute();
    ceMa.setAcceptedValues(null);

    String managedAttributeUuid = JsonAPITestHelper.extractId(
      sendPost(CollectionManagedAttributeDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        CollectionManagedAttributeDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(ceMa),
        null,
        null
      )));

    String managedAttributeKey = sendGet(CollectionManagedAttributeDto.TYPENAME, managedAttributeUuid).
      extract().body().jsonPath().get("data.attributes.key");

    CollectingEventDto ce = CollectingEventTestFixture.newEventDto();
    ce.setAttachment(null);
    ce.setCollectors(null);
    Map<String, String> extensionValues = new HashMap<>();
    extensionValues.put( "season_precpt"," avg 22 mm");
    extensionValues.put("soil_type", "earth");
    extensionValues.put("cur_land_use", "farmstead|forage crops");
    extensionValues.put("cur_vegetation", "grass");
    extensionValues.put("env_package", "Soil");
    extensionValues.put("link_climate_info", "https://climate.weather.gc.ca/climate_normals/index_e.html");
    extensionValues.put("pool_dna_extracts", "Yes; 2 x 0.25g");
    extensionValues.put("samp_vol_we_dna_ext", "0.25g");
    extensionValues.put("store_cond", "6months/-80oC");
    ce.setExtensionValues(
      Map.of("mixs_soil_v5", extensionValues,
      "mixs_soil_v4", Map.of("tillage", "abc")));
    ce.setManagedAttributes(Map.of(managedAttributeKey, "2"));

    String uuid = postCollectingEvent(ce, protocolUuid);

    findCollectingEvent(uuid).body("data.attributes.extensionValues.mixs_soil_v5", Matchers.aMapWithSize(9));

    extensionValues.remove("store_cond");
    sendPatch(ce, uuid, protocolUuid);

    findCollectingEvent(uuid).body("data.attributes.extensionValues.mixs_soil_v5", Matchers.aMapWithSize(8));
  }

  @Test
  public void bulkCreateUpdateBulkLoad_HttpOkReturned() {
    CollectingEventDto ceDto1 = CollectingEventTestFixture.newEventDto();
    CollectingEventDto ceDto2 = CollectingEventTestFixture.newEventDto();

    JsonApiBulkDocument bulkDocumentCreate = JsonApiBulkDocument.builder()
      .addData(JsonApiDocument.ResourceObject.builder()
        .type(CollectingEventDto.TYPENAME)
        .attributes(JsonAPITestHelper.toAttributeMap(ceDto1)).build())
      .addData(JsonApiDocument.ResourceObject.builder()
        .type(CollectingEventDto.TYPENAME)
        .attributes(JsonAPITestHelper.toAttributeMap(ceDto2)).build())
      .build();

    var response = sendBulkCreate(CollectingEventDto.TYPENAME, bulkDocumentCreate);
    List<String> ids = response.extract().body().jsonPath().getList("data.id");
    assertEquals(2, ids.size());

    UUID uuid1 = UUID.fromString(ids.get(0));
    UUID uuid2 = UUID.fromString(ids.get(1));

    JsonApiBulkDocument bulkDocumentUpdate = JsonApiBulkDocument.builder()
      .addData(JsonApiDocument.ResourceObject.builder()
        .type(CollectingEventDto.TYPENAME)
        .id(uuid1)
        .attributes(JsonAPITestHelper.toAttributeMap(ceDto1)).build())
      .addData(JsonApiDocument.ResourceObject.builder()
        .type(CollectingEventDto.TYPENAME)
        .id(uuid2)
        .attributes(JsonAPITestHelper.toAttributeMap(ceDto2)).build())
      .build();
    sendBulkUpdate(CollectingEventDto.TYPENAME, bulkDocumentUpdate);

    sendBulkLoad(CollectingEventDto.TYPENAME, JsonApiBulkResourceIdentifierDocument.builder()
      .addData(JsonApiDocument.ResourceIdentifier.builder()
        .type(CollectingEventDto.TYPENAME)
        .id(uuid1).build())
      .addData(JsonApiDocument.ResourceIdentifier.builder()
        .type(CollectingEventDto.TYPENAME)
        .id(uuid2).build())
      .build());
  }

  private String postCollectingEvent(CollectingEventDto ce, String protocolUuid) {
    return JsonAPITestHelper.extractId(
      sendPost(CollectingEventDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        CollectingEventDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(ce),
        JsonAPITestHelper.toRelationshipMap(
          List.of(
            JsonAPIRelationship.of("protocol", ProtocolDto.TYPENAME, protocolUuid)
          )),
        null)
      ));
  }

  private String postProtocol(ProtocolDto p) {
    return JsonAPITestHelper.extractId(
      sendPost(ProtocolDto.TYPENAME, JsonAPITestHelper.toJsonAPIMap(
        ProtocolDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(p),
        null,
        null)
      ));
  }

  private void sendPatch(CollectingEventDto body, String id,  String protocolUuid) {
    sendPatch(
      CollectingEventDto.TYPENAME, id,
      JsonAPITestHelper.toJsonAPIMap(
        CollectingEventDto.TYPENAME,
        JsonAPITestHelper.toAttributeMap(body),
        JsonAPITestHelper.toRelationshipMap(
          List.of(
            JsonAPIRelationship.of("protocol", ProtocolDto.TYPENAME, protocolUuid)
          )),
        id),
      200
    );
  }

  private ValidatableResponse findCollectingEvent(String unitId) {
    return RestAssured.given().header(CRNK_HEADER).port(this.testPort).basePath(this.basePath)
      .get(CollectingEventDto.TYPENAME + "/" + unitId).then();
  }
}
