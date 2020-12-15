package ca.gc.aafc.collection.api.repository;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.AgentRoleDto;
import ca.gc.aafc.collection.api.entities.AgentRole;
import ca.gc.aafc.collection.api.entities.AgentRole.AgentRoleType;
import ca.gc.aafc.collection.api.repository.AgentRoleRepository;
import ca.gc.aafc.collection.api.testsupport.factories.AgentRoleFactory;
import io.crnk.core.queryspec.QuerySpec;

public class AgentRoleRepositoryIT extends CollectionModuleBaseIT{
    @Inject
    private AgentRoleRepository agentRoleRepository;
  
    private AgentRole testAgentRole;

    private final String  TEST_USER ="test user";
    private final String  TEST_NAME_IN_FRENCH ="nom";    
  
    private AgentRole createTestAgentRole() {
      testAgentRole = AgentRoleFactory.newAgentRole()
      .name(Map.of("fr", TEST_NAME_IN_FRENCH))
      .agentRoleType(AgentRoleType.COLLECTING_EVENT)
      .build();  
      service.save(testAgentRole);
      return testAgentRole;
    }
  
    @BeforeEach
    public void setup(){
      createTestAgentRole();
    }    

    @Test
    public void findAgentRole_whenNoFieldsAreSelected_AgentRoleReturnedWithAllFields() {
      AgentRoleDto agentRoleDto = agentRoleRepository
          .findOne(testAgentRole.getUuid(), new QuerySpec(AgentRoleDto.class));
      assertNotNull(agentRoleDto);
      assertEquals(testAgentRole.getUuid(), agentRoleDto.getUuid());
      assertEquals(testAgentRole.getCreatedBy(), agentRoleDto.getCreatedBy());
      assertEquals(testAgentRole.getAgentRoleType(), agentRoleDto.getAgentRoleType());
      assertEquals(testAgentRole.getName(), agentRoleDto.getName());
    }      
    @Test
    public void createAgentRole_whenInvalidValue_ExceptionThrown() {
      AgentRoleDto agentRoleDto = new AgentRoleDto();
      agentRoleDto.setUuid(UUID.randomUUID());
      agentRoleDto.setAgentRoleType(AgentRoleType.COLLECTING_EVENT);
      agentRoleDto.setName(Map.of("fr", TEST_NAME_IN_FRENCH ));
  
      assertThrows(
        ConstraintViolationException.class, ()-> agentRoleRepository.create(agentRoleDto));
    }    
    
}
