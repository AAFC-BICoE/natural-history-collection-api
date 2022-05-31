package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.AccessDeniedException;

import org.junit.jupiter.api.Test;
import org.picocontainer.annotations.Inject;
import org.springframework.boot.test.context.SpringBootTest;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.collection.api.testsupport.factories.ProtocolFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.ProtocolTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;

@SpringBootTest(
        properties = "keycloak.enabled = true"
)
public class ProtocolRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private ProtocolRepository protocolRepository;

  @Test
  @WithMockKeycloakUser(username = "dev", groupRole = {ProtocolTestFixture.GROUP + ":DINA_ADMIN"})
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    ProtocolDto pt = ProtocolTestFixture.newProtocol();
    ProtocolDto result = protocolRepository.findOne(
            protocolRepository.create(pt).getUuid(),
            new QuerySpec(ProtocolDto.class));

    assertNotNull(result.getCreatedBy());
    assertEquals(pt.getName(), result.getName());
    assertEquals(pt.getGroup(), result.getGroup());
  }

  @Test
  @WithMockKeycloakUser(username = "other user", groupRole = {"notAAFC: staff"})
  public void updateFromDifferentGroup_throwAccessDenied() {
    Protocol testProtocol = ProtocolFactory.newProtocol()
            .group("preparation process definition")
            .name("aafc")
            .build();
    protocolService.create(testProtocol);
    ProtocolDto retrievedProtocol = protocolRepository.findOne(testProtocol.getUuid(),
            new QuerySpec(ProtocolDto.class));
    assertThrows(AccessDeniedException.class, () -> protocolRepository.save(retrievedProtocol));
  }
}

