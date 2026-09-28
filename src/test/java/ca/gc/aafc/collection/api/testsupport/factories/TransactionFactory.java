package ca.gc.aafc.collection.api.testsupport.factories;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ca.gc.aafc.collection.api.entities.AgentRolesWithDate;
import ca.gc.aafc.collection.api.entities.Transaction;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class TransactionFactory implements TestableEntityFactory<Transaction> {

  public static final String GROUP = "test group";

  @Override
  public Transaction getEntityInstance() {
    return newTransaction().build();
  }

  public static Transaction.TransactionBuilder newTransaction() {
    AgentRolesWithDate agentRole1 = AgentRolesWithDateFactory.newAgentRoles().build();
    AgentRolesWithDate agentRole2 = AgentRolesWithDateFactory.newAgentRoles().build();
    return Transaction.builder()
        .uuid(UUID.randomUUID())
        .group(GROUP)
        .materialDirection(Transaction.Direction.IN)
        .attachment(List.of(UUID.randomUUID()))
        .agentRoles(new ArrayList<>(List.of(agentRole1, agentRole2)))
        .transactionNumber(TestableEntityFactory.generateRandomNameLettersOnly(12));
  }
}
