package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.Institution;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionMethodTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.InstitutionFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import javax.inject.Inject;
import java.util.UUID;

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
    Assertions.assertEquals(collectionDto.getMultilingualDescription().getDescriptions().get(0).getLang(),
      result.getMultilingualDescription().getDescriptions().get(0).getLang());
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:COLLECTION_MANAGER"})
  public void create_WhenManager_AccessDeniedException() {
    CollectionDto collectionDto = newCollectionDto();
    assertThrows(AccessDeniedException.class, () -> collectionRepository.create(collectionDto));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:DINA_ADMIN"})
  public void delete_WhenAdmin_AccessAccepted() {
    Collection persisted = setupPersistedCollection();
    assertDoesNotThrow(() -> collectionRepository.delete(persisted.getUuid()));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:COLLECTION_MANAGER"})
  public void delete_WhenManager_AccessDeniedException() {
    Collection persisted = setupPersistedCollection();
    assertThrows(AccessDeniedException.class, () -> collectionRepository.delete(persisted.getUuid()));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:DINA_ADMIN"})
  public void update_WhenAdmin_AccessAccepted() {
    assertDoesNotThrow(() -> collectionRepository.save(collectionRepository.findOne(
      collectionRepository.create(newCollectionDto()).getUuid(),
      new QuerySpec(CollectionDto.class))));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:COLLECTION_MANAGER"})
  public void update_WhenManager_AccessDeniedException() {
    CollectionDto collectionDto = newCollectionDto();
    assertThrows(AccessDeniedException.class, () -> collectionRepository.save(collectionDto));
  }

  private CollectionDto newCollectionDto() {
    Institution institution = setupPersistedInstitution();
    CollectionDto collectionDto = new CollectionDto();
    collectionDto.setName(name);
    collectionDto.setInstitution(InstitutionFixture.newInstitution().uuid(institution.getUuid()).build());
    collectionDto.setMultilingualDescription(CollectionMethodTestFixture.newMulti());
    collectionDto.setGroup(group);
    collectionDto.setCode(code);
    return collectionDto;
  }

  /**
   * Setup with service to bypass authorization for tests.
   *
   * @return the persisted collection
   */
  private Collection setupPersistedCollection() {
    Institution institution = setupPersistedInstitution();
    Collection persisted = Collection.builder()
      .name("name")
      .institution(institution)
      .uuid(UUID.randomUUID())
      .group("group")
      .createdBy("by")
      .code("DNA")
      .build();
    collectionService.create(persisted);
    return persisted;
  }

  /**
   * Setup with service to bypass Admin only authorization.
   * @return persisted institution
   */
  private Institution setupPersistedInstitution() {
    Institution institution = InstitutionFixture.newInstitutionEntity().build();
    service.save(institution);
    return institution;
  }
}
