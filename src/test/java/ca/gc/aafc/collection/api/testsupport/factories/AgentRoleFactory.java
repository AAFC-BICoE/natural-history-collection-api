package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.AgentRole;
import ca.gc.aafc.collection.api.entities.AgentRole.AgentRoleType;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

import java.util.UUID;
import com.google.common.collect.ImmutableMap;

public class AgentRoleFactory implements TestableEntityFactory<AgentRole> {

    @Override
    public AgentRole getEntityInstance() {
      return newAgentRole().build();
    }

    /**
     * Static method that can be called to return a configured builder that can be
     * further customized to return the actual entity object, call the .build()
     * method on a builder.
     *
     * @return Pre-configured builder with all mandatory fields set
     */
    public static AgentRole.AgentRoleBuilder newAgentRole() {
      return AgentRole
          .builder()
          .uuid(UUID.randomUUID())
          .name(ImmutableMap.of("en", "test name"))
          .createdBy("createdBy")
          .agentRoleType(AgentRoleType.COLLECTING_EVENT);          
    }
  }
