package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.TransactionDto;
import ca.gc.aafc.collection.api.entities.AgentRolesWithDate;
import ca.gc.aafc.collection.api.entities.Transaction;
import ca.gc.aafc.collection.api.testsupport.factories.AgentRolesWithDateFactory;
import ca.gc.aafc.dina.dto.ExternalRelationDto;

public class TransactionFixture {
  public static final String GROUP = "group 1";

  private TransactionFixture() {
  }

  public static TransactionDto.TransactionDtoBuilder newTransaction() {

    AgentRolesWithDate agentRole1 = AgentRolesWithDateFactory.newAgentRoles().build();
    AgentRolesWithDate agentRole2 = AgentRolesWithDateFactory.newAgentRoles().build();

    return TransactionDto.builder()
        .agentRoles(new ArrayList<AgentRolesWithDate>(List.of(
            agentRole1, agentRole2)))
        .materialDirection(Transaction.Direction.IN)
        .transactionNumber(RandomStringUtils.randomAlphabetic(12))
        .otherIdentifiers(List.of("T2123", "P245643"))
        .managedAttributes(new HashMap<>())
        .attachment(List.of(ExternalRelationDto.builder()
            .id(UUID.randomUUID().toString())
            .type("metadata")
            .build()))
        .group(GROUP)
        .createdBy("TransactionFixture");
  }
}
