package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.FieldExtensionValueDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.FieldExtensionValueTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(properties = "keycloak.enabled=true")
public class FieldExtensionValueRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private FieldExtensionValueRepository repo;

  @Test
  @WithMockKeycloakUser(groupRole = FieldExtensionValueTestFixture.GROUP + ":SUPER_USER")
  void findOneByKey_whenKeyProvided_fieldExtensionValueFetched() {
    FieldExtensionValueDto fieldExtensionValueDto = FieldExtensionValueTestFixture.newFieldExtensionValueDto();
    FieldExtensionValueDto fetchedFieldExtensionValue = repo.findOne(FieldExtensionValueTestFixture.FIELD_EXTENSION_KEY);
    assertEquals(fetchedFieldExtensionValue.getExtensionKey(), fieldExtensionValueDto.getExtensionKey());
    assertEquals(fetchedFieldExtensionValue.getExtensionName(), fieldExtensionValueDto.getExtensionName());
    assertEquals(fetchedFieldExtensionValue.getField().getName(), fieldExtensionValueDto.getField().getName());
    assertEquals(fetchedFieldExtensionValue.getField().getKey(), fieldExtensionValueDto.getField().getKey());
    assertEquals(fetchedFieldExtensionValue.getField().getDinaComponent(), fieldExtensionValueDto.getField().getDinaComponent());
  }
}
