package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.FieldExtensionValueDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionManagedAttributeTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.FieldExtensionValueTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement.VocabularyElementType;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.util.UUID;

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
    Assertions.assertEquals(fetchedFieldExtensionValue.getExtensionKey(), fieldExtensionValueDto.getExtensionKey());
    Assertions.assertEquals(fetchedFieldExtensionValue.getExtensionName(), fieldExtensionValueDto.getExtensionName());
    Assertions.assertEquals(fetchedFieldExtensionValue.getField().getName(), fieldExtensionValueDto.getField().getName());
    Assertions.assertEquals(fetchedFieldExtensionValue.getField().getKey(), fieldExtensionValueDto.getField().getKey());
    Assertions.assertEquals(fetchedFieldExtensionValue.getField().getDinaComponent(), fieldExtensionValueDto.getField().getDinaComponent());
  }
}
