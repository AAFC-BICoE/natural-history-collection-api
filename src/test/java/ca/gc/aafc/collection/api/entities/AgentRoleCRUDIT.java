package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.AgentRole.AgentRoleType;
import ca.gc.aafc.collection.api.testsupport.factories.AgentRoleFactory;
import ca.gc.aafc.dina.testsupport.DatabaseSupportService;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import java.time.LocalDateTime;
import com.google.common.collect.ImmutableMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AgentRoleCRUDIT extends CollectionModuleBaseIT {
  private final String  TEST_USER ="test user";
  private final String  TEST_NAME_IN_FRENCH ="nom";

  @Inject
  private DatabaseSupportService dbService;

  private AgentRole agentRole = AgentRoleFactory.newAgentRole().build();  

  @Test
  public void testSave() {
    assertNull(agentRole.getId());
    dbService.save(agentRole);
    assertNotNull(agentRole.getId());
  }

  @Test
  public void testFind() {
    AgentRole agentRole = AgentRoleFactory.newAgentRole()
      .name(ImmutableMap.of("fr", TEST_NAME_IN_FRENCH))
      .createdBy(TEST_USER)
      .build();          
    dbService.save(agentRole);

    AgentRole fetchedAgentRole = dbService.find(AgentRole.class, agentRole.getId());
    assertEquals(agentRole.getId(), fetchedAgentRole.getId());
    assertEquals(TEST_USER, fetchedAgentRole.getCreatedBy());
    assertEquals(AgentRoleType.COLLECTING_EVENT, fetchedAgentRole.getAgentRoleType());
    assertEquals(TEST_NAME_IN_FRENCH, fetchedAgentRole.getName().get("fr"));
    assertNotNull(fetchedAgentRole.getCreatedOn());
  }
}
