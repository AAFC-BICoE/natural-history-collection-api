package ca.gc.aafc.collection.api.repository;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import ca.gc.aafc.collection.api.CollectionModuleApiLauncher;
import ca.gc.aafc.dina.dto.JsonApiResource;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.DinaRepositoryV2;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.PostgresTestContainerInitializer;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

import java.util.UUID;
import java.util.function.Function;

/**
 * No Transactional annotation by design.
 * Repository level tests should not use it.
 */
@SpringBootTest(classes = CollectionModuleApiLauncher.class, properties = "keycloak.enabled = true")
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.yml")
@ContextConfiguration(initializers = { PostgresTestContainerInitializer.class })
@Import(CollectionModuleBaseRepositoryIT.CollectionModuleTestConfiguration.class)
public class BaseRepositoryIT {

  protected UUID createWithRepository(JsonApiResource dto, DinaRepositoryV2<?, ?> repo) {
    JsonApiDocument organismToCreate = JsonApiDocuments.createJsonApiDocument(
      null, dto.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(dto)
    );

    return JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(repo
      .handleCreate(organismToCreate, null));
  }

  protected UUID createWithRepository(JsonApiResource dto, Function<JsonApiDocument, ResponseEntity<RepresentationModel<?>>> onCreateMethod) {
    JsonApiDocument organismToCreate = JsonApiDocuments.createJsonApiDocument(
      null, dto.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(dto)
    );

    return JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(onCreateMethod.apply(organismToCreate));
  }
}
