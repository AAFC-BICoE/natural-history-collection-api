package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectorGroupDto;
import ca.gc.aafc.collection.api.entities.CollectorGroup;
import ca.gc.aafc.collection.api.testsupport.factories.CollectorGroupFactory;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectorGroupRepositoryIT extends CollectionModuleBaseIT{
    @Inject
    private CollectorGroupRepository collectorGroupRepository;
  
    private CollectorGroup testCollectorGroup;

    private List<UUID> identifiers = new ArrayList<UUID>();
    private UUID firstAgentIdentifier = UUID.randomUUID();
    private UUID secondAgentIdentifier = UUID.randomUUID();    

    private final String TEST_COLLECTOR_GROUP = "test collector group";
    private final String TEST_NEW_COLLECTOR_GROUP = "new test collector group";
  
    private CollectorGroup createTestCollectorGroup() {            
      testCollectorGroup = CollectorGroupFactory.newCollectorGroup()
        .name(TEST_COLLECTOR_GROUP)
        .agentIdentifiers(identifiers)
        .build();
  
      service.save(testCollectorGroup);
      return testCollectorGroup;
    }
  
    @BeforeEach
    public void setup(){
      identifiers.clear();
      identifiers = List.of(firstAgentIdentifier, secondAgentIdentifier);            
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
      assertEquals(2, collectorGroupDto.getAgentIdentifiers().size());
      assertEquals(firstAgentIdentifier.toString(), collectorGroupDto.getAgentIdentifiers().get(0).getId());
    }    
    
    @WithMockKeycloakUser(username = "test user")  
    @Test
    public void create_WithAuthenticatedUser_SetsCreatedBy() {
      CollectorGroupDto cg = new CollectorGroupDto();
      cg.setUuid(UUID.randomUUID());
      cg.setName(TEST_NEW_COLLECTOR_GROUP);
      cg.setAgentIdentifiers(List.of(
        ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("agent").build()));
      CollectorGroupDto result = collectorGroupRepository.findOne(collectorGroupRepository.create(cg).getUuid(),
          new QuerySpec(CollectorGroupDto.class));
      assertNotNull(result.getCreatedBy());
    }
          
}
