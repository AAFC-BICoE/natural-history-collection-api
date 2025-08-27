package ca.gc.aafc.collection.api.repository;

import javax.inject.Inject;
import java.util.UUID;

import ca.gc.aafc.collection.api.CollectionModuleKeycloakBaseIT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.dto.ExpeditionDto;
import ca.gc.aafc.collection.api.entities.Expedition;
import ca.gc.aafc.collection.api.repository.ExpeditionRepository;
import ca.gc.aafc.collection.api.testsupport.factories.ExpeditionFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.ExpeditionTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ExpeditionRepositoryIT extends CollectionModuleKeycloakBaseIT {

  @Inject
  private ExpeditionRepository expeditionRepository;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {"aafc:user"})
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    ExpeditionDto expedition = ExpeditionTestFixture.newExpedition();
    UUID expeditionUUID = createWithRepository(expedition, expeditionRepository::onCreate);
    ExpeditionDto result = expeditionRepository.getOne(expeditionUUID, null).getDto().getDetermination().getFirst();
    assertNotNull(result.getCreatedBy());
    assertEquals(expedition.getName(), result.getName());
    assertEquals(expedition.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC:user"})
  public void updateFromDifferentGroup_throwAccessDenied() {
    Expedition testExpedition = ExpeditionFactory.newExpedition()
      .group("some test group")
      .name("aafc")
      .build();
    expeditionService.create(testExpedition);
    ExpeditionDto retrievedExpedition = expeditionRepository.findOne(testExpedition.getUuid(),
      new QuerySpec(ExpeditionDto.class));
      Assertions.assertThrows(AccessDeniedException.class, () -> expeditionRepository.save(retrievedExpedition));
  }
}
