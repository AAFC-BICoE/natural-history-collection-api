package ca.gc.aafc.collection.api.openapi;

import java.util.Map;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.collection.api.config.TestConfigProperties;
import ca.gc.aafc.collection.api.dto.TransactionDto;
import ca.gc.aafc.collection.api.entities.Transaction;
import ca.gc.aafc.collection.api.testsupport.fixtures.ShipmentTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.TransactionFixture;
import ca.gc.aafc.dina.testsupport.BaseRestAssuredTest;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.TransactionTestingHelper;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.specs.OpenAPI3Assertions;
import io.restassured.response.ValidatableResponse;
import jakarta.inject.Inject;

@SpringBootTest(
  classes = CollectionModuleApiLauncher.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = {PostgresTestContainerInitializer.class})
@Import(TestConfigProperties.class)
public class TransactionOpenApiIT extends BaseRestAssuredTest {
    
  public static final String API_BASE_PATH = "/api/v1/transaction";
  private static final String SCHEMA_NAME = "Transaction";

  @Inject
  private DatabaseSupportService databaseSupportService;

  @Inject
  private TransactionTestingHelper transactionTestingHelper;

  protected TransactionOpenApiIT() {
    super(API_BASE_PATH);
  }

  @Test
  public void post_NewTransaction_ReturnsOkayAndBody() {
    // Generate post response.
    ValidatableResponse response = sendPost("",
        JsonAPITestHelper.toJsonAPIMap(
            TransactionDto.TYPENAME,
            JsonAPITestHelper.toAttributeMap(TransactionFixture.newTransaction()
                .attachment(null) // Will be added as a relationship.
                .materialSamples(null) // Will be added as a relationship.
                .shipment(ShipmentTestFixture.newShipment().build())
                .build()
            ),
            Map.of(
              "materialSamples", JsonAPITestHelper.generateExternalRelationList("external-material-sample", 1),
              "attachment", JsonAPITestHelper.generateExternalRelationList("metadata", 1)
            ),
            null
        )
    );

    System.out.println(response.extract().asPrettyString());

    // Validate the response against the specs.
    response.body("data.id", Matchers.notNullValue());
    OpenAPI3Assertions.assertRemoteSchema(OpenAPIConstants.COLLECTION_API_SPECS_URL, SCHEMA_NAME,
            response.extract().asString());

    // Cleanup:
    UUID uuid = response.extract().jsonPath().getUUID("data.id");
    transactionTestingHelper.doInTransactionWithoutResult(
        operation -> databaseSupportService.deleteByProperty(Transaction.class, "uuid", uuid));
  }

}
