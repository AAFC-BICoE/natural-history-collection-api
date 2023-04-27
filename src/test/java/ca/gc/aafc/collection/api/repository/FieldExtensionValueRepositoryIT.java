package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.FieldExtensionValueDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.FieldExtensionValueTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;
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
  void findOneByKey_whenKeyProvided_managedAttributeFetched() {
    FieldExtensionValueDto fieldExtensionValueDto = FieldExtensionValueTestFixture.newFieldExtensionValueDto();
    QuerySpec querySpec = new QuerySpec(FieldExtensionValueDto.class);
    FieldExtensionValueDto fetchedFieldExtensionValue = repo.findOne("mixs_microbial_v4.alkyl_diethers", querySpec);
    assertEquals(fetchedFieldExtensionValue.getExtensionKey(), fieldExtensionValueDto.getExtensionKey());
    assertEquals(fetchedFieldExtensionValue.getExtensionName(), fieldExtensionValueDto.getExtensionName());
    assertEquals(fetchedFieldExtensionValue.getField().getName(), fieldExtensionValueDto.getField().getName());
    assertEquals(fetchedFieldExtensionValue.getField().getKey(), fieldExtensionValueDto.getField().getKey());
    assertEquals(fetchedFieldExtensionValue.getField().getDinaComponent(), fieldExtensionValueDto.getField().getDinaComponent());
  }
}
