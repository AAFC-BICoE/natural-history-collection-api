package ca.gc.aafc.collection.api.repository;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import ca.gc.aafc.collection.api.dto.CollectionDto;
import ca.gc.aafc.collection.api.entities.Collection;
import ca.gc.aafc.collection.api.entities.Institution;
import ca.gc.aafc.collection.api.service.CollectionService;
import ca.gc.aafc.collection.api.service.InstitutionService;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectionFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.InstitutionFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.MultilingualTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import javax.inject.Inject;

public class CollectionRepositoryIT extends BaseRepositoryIT {

  @Inject
  private CollectionRepository collectionRepository;

  @Inject
  private ServiceTransactionWrapper serviceTransactionWrapper;

  @Inject
  protected CollectionService collectionService;

  @Inject
  protected InstitutionService institutionService;

  private static final String group = "cnc";
  private static final String code = "YUL";

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:DINA_ADMIN"})
  public void create_WhenAdmin_SetsCreatedBy()
    throws ResourceGoneException, ResourceNotFoundException {
    CollectionDto collectionDto = newCollectionDto();

    UUID collUUID = createWithRepository(collectionDto, collectionRepository::onCreate);

    CollectionDto result = collectionRepository.getOne(collUUID, null).getDto();
    assertNotNull(result.getCreatedBy());
    assertEquals(collectionDto.getName(), result.getName());
    assertEquals(collectionDto.getGroup(), result.getGroup());
    assertEquals(collectionDto.getCode(), result.getCode());
    assertEquals(collectionDto.getMultilingualDescription().getDescriptions().getFirst().getLang(),
      result.getMultilingualDescription().getDescriptions().getFirst().getLang());
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"AAFC:SUPER_USER"})
  public void create_WhenDifferentGroup_AccessDeniedException() {
    CollectionDto collectionDto = newCollectionDto();
    assertThrows(AccessDeniedException.class, () -> createWithRepository(collectionDto, collectionRepository));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"AAFC:SUPER_USER"})
  public void delete_WhenDifferentGroup_AccessDeniedException() {
    Collection persisted = setupPersistedCollection();
    assertThrows(AccessDeniedException.class, () -> collectionRepository.onDelete(persisted.getUuid()));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:DINA_ADMIN"})
  public void delete_WhenAdmin_AccessAccepted() {
    Collection persisted = setupPersistedCollection();
    assertDoesNotThrow(() -> collectionRepository.onDelete(persisted.getUuid()));
  }
    
  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:SUPER_USER"})
  public void delete_WhenSuperUser_AccessAccepted() {
    Collection persisted = setupPersistedCollection();
    assertDoesNotThrow(() -> collectionRepository.onDelete(persisted.getUuid()));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:DINA_ADMIN"})
  public void update_WhenAdmin_AccessAccepted() throws ResourceGoneException, ResourceNotFoundException {

    UUID collUUID = createWithRepository(newCollectionDto(), collectionRepository::onCreate);

    CollectionDto collDto = collectionRepository.getOne(collUUID, "").getDto();
    JsonApiDocument toUpdate = JsonApiDocuments.createJsonApiDocument(
      collUUID, collDto.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(collDto)
    );

    assertDoesNotThrow(() -> collectionRepository.onUpdate(toUpdate, collUUID));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:SUPER_USER"})
  public void update_WhenSuperUser_AccessAccepted() throws ResourceGoneException, ResourceNotFoundException {
    UUID collUUID = createWithRepository(newCollectionDto(), collectionRepository::onCreate);

    CollectionDto collDto = collectionRepository.getOne(collUUID, "").getDto();
    JsonApiDocument toUpdate = JsonApiDocuments.createJsonApiDocument(
      collUUID, collDto.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(collDto)
    );

    assertDoesNotThrow(() -> collectionRepository.onUpdate(toUpdate, collUUID));
  }

  private CollectionDto newCollectionDto() {
    return CollectionFixture.newCollection()
      .group(group)
      .code(code)
      .multilingualDescription(MultilingualTestFixture.newMultilingualDescription())
      .build();
  }

  /**
   * Setup with service to bypass authorization for tests.
   *
   * @return the persisted collection
   */
  @Transactional
  private Collection setupPersistedCollection() {
    Institution institution = setupPersistedInstitution();
    Collection persisted = Collection.builder()
      .name(RandomStringUtils.randomAlphabetic(12))
      .institution(institution)
      .uuid(UUID.randomUUID())
      .group(group)
      .createdBy("by")
      .code("DNA")
      .build();
    serviceTransactionWrapper.execute(collectionService::create, persisted);
    return persisted;
  }

  /**
   * Setup with service to bypass Admin only authorization.
   * @return persisted institution
   */
  private Institution setupPersistedInstitution() {
    Institution institution = InstitutionFixture.newInstitutionEntity().build();
    serviceTransactionWrapper.execute(institutionService::create, institution);
    return institution;
  }
}
