package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectionRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private CollectionRepository collectionRepository;

  private static final String group = "aafc";
  private static final String name = "preparation process definition";
  private static final String code = "YUL";

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:DINA_ADMIN"})
  public void create_WhenAdmin_SetsCreatedBy() {
    CollectionDto collectionDto = newCollectionDto();
    CollectionDto result = collectionRepository.findOne(
      collectionRepository.create(collectionDto).getUuid(),
      new QuerySpec(CollectionDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(collectionDto.getName(), result.getName());
    assertEquals(collectionDto.getGroup(), result.getGroup());
    assertEquals(collectionDto.getCode(), result.getCode());
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:COLLECTION_MANAGER"})
  public void create_WhenManager_AccessDeniedException() {
    CollectionDto collectionDto = newCollectionDto();
    assertThrows(AccessDeniedException.class, () -> collectionRepository.create(collectionDto));
  }

  private CollectionDto newCollectionDto() {
    CollectionDto collectionDto = new CollectionDto();
    collectionDto.setName(name);
    collectionDto.setGroup(group);
    collectionDto.setCode(code);
    return collectionDto;
  }

}
