package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionManagedAttributeTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.dina.vocabulary.TypedVocabularyElement.VocabularyElementType;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.util.UUID;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectionCollectionManagedAttributeRepoIT extends CollectionModuleBaseIT {

  @Inject
  private CollectionManagedAttributeRepo repo;

  @Test
  @WithMockKeycloakUser(groupRole = "dinaGroup:SUPER_USER")
  void create_recordCreated() {
    String expectedName = "dina attribute #12";
    String expectedValue = "dina value";
    String expectedCreatedBy = "dina";
    String expectedGroup = "dina-group";

    CollectionManagedAttributeDto dto = CollectionManagedAttributeTestFixture.newCollectionManagedAttribute();
    dto.setName(expectedName);
    dto.setVocabularyElementType(VocabularyElementType.INTEGER);
    dto.setAcceptedValues(new String[]{expectedValue});
    dto.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);
    dto.setCreatedBy(expectedCreatedBy);
    dto.setGroup(expectedGroup);

    UUID uuid = repo.create(dto).getUuid();
    CollectionManagedAttributeDto result = repo.findOne(uuid, new QuerySpec(
        CollectionManagedAttributeDto.class));
    Assertions.assertEquals(uuid, result.getUuid());
    Assertions.assertEquals(expectedName, result.getName());
    Assertions.assertEquals("dina_attribute_12", result.getKey());
    Assertions.assertEquals(expectedValue, result.getAcceptedValues()[0]);
    Assertions.assertNotNull(result.getCreatedBy());
    Assertions.assertEquals(expectedGroup, result.getGroup());
    Assertions.assertEquals(VocabularyElementType.INTEGER, result.getVocabularyElementType());
    Assertions.assertEquals(
      CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT,
      result.getManagedAttributeComponent());
  }

  @Test
  @WithMockKeycloakUser(groupRole = CollectionManagedAttributeTestFixture.GROUP + ":SUPER_USER")
  void findOneByKey_whenKeyProvided_managedAttributeFetched() {
    CollectionManagedAttributeDto newAttribute = CollectionManagedAttributeTestFixture.newCollectionManagedAttribute();
    newAttribute.setName("Collecting Event Attribute 1");
    newAttribute.setVocabularyElementType(VocabularyElementType.INTEGER);
    newAttribute.setManagedAttributeComponent(CollectionManagedAttribute.ManagedAttributeComponent.COLLECTING_EVENT);

    UUID newAttributeUuid = repo.create(newAttribute).getUuid();

    QuerySpec querySpec = new QuerySpec(CollectionManagedAttributeDto.class);
    CollectionManagedAttributeDto fetchedAttribute = repo.findOne("collecting_event.collecting_event_attribute_1", querySpec);

    Assertions.assertEquals(newAttributeUuid, fetchedAttribute.getUuid());
  }
}
