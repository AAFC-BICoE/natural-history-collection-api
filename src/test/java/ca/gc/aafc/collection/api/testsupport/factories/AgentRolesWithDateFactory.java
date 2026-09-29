package ca.gc.aafc.collection.api.testsupport.factories;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.entities.AgentRolesWithDate;

public class AgentRolesWithDateFactory {
  public static AgentRolesWithDate.AgentRolesWithDateBuilder<?, ?> newAgentRoles() {
    return AgentRolesWithDate.builder()
        .agent(UUID.randomUUID())
        .roles(new ArrayList<>(List.of("Role1", "Role2")))
        .date(LocalDate.now())
        .remarks(RandomStringUtils.randomAlphabetic(30));
  }
}
