package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashSet;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectorGroupDto;
import ca.gc.aafc.collection.api.entities.CollectorGroup;
import ca.gc.aafc.collection.api.repository.CollectorGroupRepository;
import ca.gc.aafc.collection.api.testsupport.factories.CollectorGroupFactory;
import io.crnk.core.queryspec.QuerySpec;

public class CollectorGroupRepositoryIT extends CollectionModuleBaseIT{
    @Inject
    private CollectorGroupRepository collectorGroupRepository;
  
    private CollectorGroup testCollectorGroup;

    private UUID firstAgentIdentifier = UUID.randomUUID();
    private UUID secondAgentIdentifier = UUID.randomUUID();
    private LinkedHashSet<UUID> identifiers = new LinkedHashSet<UUID>();
  
    private CollectorGroup createTestCollectorGroup() {            
      testCollectorGroup = CollectorGroupFactory.newCollectorGroup()
        .name("test collector group")
        .agentIdentifiers(identifiers)
        .build();
  
      service.save(testCollectorGroup);
      return testCollectorGroup;
    }
  
    @BeforeEach
    public void setup(){
      identifiers.clear();
      identifiers.add(firstAgentIdentifier);
      identifiers.add(secondAgentIdentifier);            
      createTestCollectorGroup();
    }    

    @Test
    public void findCollectorGroup_whenNoFieldsAreSelected_CollectorGroupReturnedWithAllFields() {
      CollectorGroupDto collectorGroupDto = collectorGroupRepository
          .findOne(testCollectorGroup.getUuid(), new QuerySpec(CollectorGroupDto.class));
      assertNotNull(collectorGroupDto);
      assertEquals(testCollectorGroup.getUuid(), collectorGroupDto.getUuid());
      assertEquals(testCollectorGroup.getCreatedBy(), collectorGroupDto.getCreatedBy());
      assertEquals(testCollectorGroup.getName(), collectorGroupDto.getName());
      assertEquals(testCollectorGroup.getAgentIdentifiers(), collectorGroupDto.getAgentIdentifiers());
    }    
    
    @Test
    public void create_WithAuthenticatedUser_SetsCreatedBy() {
      CollectorGroupDto cg = new CollectorGroupDto();
      cg.setUuid(UUID.randomUUID());
      cg.setName("new test collector group");
      cg.setAgentIdentifiers(identifiers);
      CollectorGroupDto result = collectorGroupRepository.findOne(collectorGroupRepository.create(os).getUuid(),
          new QuerySpec(CollectorGroupDto.class));
      assertEquals(result.getCreatedBy());
    }
          
}
