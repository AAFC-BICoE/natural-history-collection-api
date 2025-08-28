package ca.gc.aafc.collection.api.repository;

import javax.inject.Inject;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import ca.gc.aafc.collection.api.dto.ExpeditionDto;
import ca.gc.aafc.collection.api.entities.Expedition;
import ca.gc.aafc.collection.api.service.ExpeditionService;
import ca.gc.aafc.collection.api.repository.ExpeditionRepository;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.factories.ExpeditionFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.ExpeditionFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.MultilingualTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;

import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ExpeditionRepositoryIT extends BaseRepositoryIT {

  @Inject
  private ExpeditionRepository expeditionRepository;

  @Inject
  private ServiceTransactionWrapper serviceTransactionWrapper;

  @Inject
  protected ExpeditionService expeditionService;

  private static final String group = "cnc";

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:DINA_ADMIN"})
  public void create_WhenAdmin_SetsCreatedBy()
    throws ResourceGoneException, ResourceNotFoundException {
    ExpeditionDto expeditionDto = newExpeditionDto();

    UUID collUUID = createWithRepository(expeditionDto, expeditionRepository::onCreate);

    ExpeditionDto result = expeditionRepository.getOne(collUUID, null).getDto();
    assertNotNull(result.getCreatedBy());
    assertEquals(expeditionDto.getName(), result.getName());
    assertEquals(expeditionDto.getGroup(), result.getGroup());
    assertEquals(expeditionDto.getGeographicContext(), result.getGeographicContext());
    assertEquals(expeditionDto.getMultilingualDescription().getDescriptions().getFirst().getLang(),
      result.getMultilingualDescription().getDescriptions().getFirst().getLang());
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"AAFC:SUPER_USER"})
  public void create_WhenDifferentGroup_AccessDeniedException() {
    ExpeditionDto expeditionDto = newExpeditionDto();
    assertThrows(AccessDeniedException.class, () -> createWithRepository(expeditionDto, expeditionRepository));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"AAFC:SUPER_USER"})
  public void delete_WhenDifferentGroup_AccessDeniedException() {
    Expedition persisted = setupPersistedExpedition();
    assertThrows(AccessDeniedException.class, () -> expeditionRepository.onDelete(persisted.getUuid()));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:DINA_ADMIN"})
  public void delete_WhenAdmin_AccessAccepted() {
    Expedition persisted = setupPersistedExpedition();
    assertDoesNotThrow(() -> expeditionRepository.onDelete(persisted.getUuid()));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:SUPER_USER"})
  public void delete_WhenSuperUser_AccessAccepted() {
    Expedition persisted = setupPersistedExpedition();
    assertDoesNotThrow(() -> expeditionRepository.onDelete(persisted.getUuid()));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:DINA_ADMIN"})
  public void update_WhenAdmin_AccessAccepted() throws ResourceGoneException, ResourceNotFoundException {

    UUID collUUID = createWithRepository(newExpeditionDto(), expeditionRepository::onCreate);

    ExpeditionDto collDto = expeditionRepository.getOne(collUUID, "").getDto();
    JsonApiDocument toUpdate = JsonApiDocuments.createJsonApiDocument(
      collUUID, collDto.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(collDto)
    );

    assertDoesNotThrow(() -> expeditionRepository.onUpdate(toUpdate, collUUID));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"CNC:SUPER_USER"})
  public void update_WhenSuperUser_AccessAccepted() throws ResourceGoneException, ResourceNotFoundException {
    UUID collUUID = createWithRepository(newExpeditionDto(), expeditionRepository::onCreate);

    ExpeditionDto collDto = expeditionRepository.getOne(collUUID, "").getDto();
    JsonApiDocument toUpdate = JsonApiDocuments.createJsonApiDocument(
      collUUID, collDto.getJsonApiType(),
      JsonAPITestHelper.toAttributeMap(collDto)
    );

    assertDoesNotThrow(() -> expeditionRepository.onUpdate(toUpdate, collUUID));
  }

  private ExpeditionDto newExpeditionDto() {
    return ExpeditionFixture.newExpedition()
      .group(group)
      .multilingualDescription(MultilingualTestFixture.newMultilingualDescription())
      .build();
  }

  /**
   * Setup with service to bypass authorization for tests.
   *
   * @return the persisted collection
   */
  @Transactional
  private Expedition setupPersistedExpedition() {
    Expedition persisted = Expedition.builder()
      .name(RandomStringUtils.randomAlphabetic(12))
      .uuid(UUID.randomUUID())
      .group(group)
      .createdBy("by")
      .geographicContext("Antarctic")
      .build();
    serviceTransactionWrapper.execute(expeditionService::create, persisted);
    return persisted;
  }

}
