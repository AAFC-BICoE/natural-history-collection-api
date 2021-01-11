package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.CollectorGroupDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.CollectorGroup;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.CollectorGroupFactory;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CollectorGroupRepositoryIT extends CollectionModuleBaseIT {
  @Inject
  private CollectorGroupRepository collectorGroupRepository;

  private CollectorGroup testCollectorGroup;
  private CollectingEvent testCollectingEvent;

  private final List<UUID> identifiers = new ArrayList<>();
  private final UUID firstAgentIdentifier = UUID.randomUUID();
  private final UUID secondAgentIdentifier = UUID.randomUUID();

  @BeforeEach
  public void setup() {
    testCollectingEvent = createEvent();
    identifiers.clear();
    identifiers.add(firstAgentIdentifier);
    identifiers.add(secondAgentIdentifier);
    createTestCollectorGroup(testCollectingEvent);
  }

  private void createTestCollectorGroup(CollectingEvent testCollectingEvent) {
    testCollectorGroup = CollectorGroupFactory.newCollectorGroup()
      .name("test collector group")
      .agentIdentifiers(identifiers.toArray(new UUID[0]))
      .collectingEvents(List.of(testCollectingEvent))
      .build();
    service.save(testCollectorGroup);
  }

  private CollectingEvent createEvent() {
    CollectingEvent event = CollectingEventFactory.newCollectingEvent().build();
    service.save(event);
    return event;
  }

  @Test
  public void findCollectorGroup_whenNoFieldsAreSelected_CollectorGroupReturnedWithAllFields() {
    CollectorGroupDto collectorGroupDto = collectorGroupRepository
      .findOne(testCollectorGroup.getUuid(), new QuerySpec(CollectorGroupDto.class));
    assertNotNull(collectorGroupDto);
    assertEquals(testCollectorGroup.getUuid(), collectorGroupDto.getUuid());
    assertEquals(testCollectorGroup.getCreatedBy(), collectorGroupDto.getCreatedBy());
    assertEquals(testCollectorGroup.getName(), collectorGroupDto.getName());
    assertEquals(2, collectorGroupDto.getAgentIdentifiers().length);
    assertEquals(firstAgentIdentifier, collectorGroupDto.getAgentIdentifiers()[0]);
    assertEquals(testCollectingEvent.getUuid(), collectorGroupDto.getCollectingEvents().get(0).getUuid());
  }

  @Test
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    CollectingEventDto event = new CollectingEventDto();
    event.setUuid(testCollectingEvent.getUuid());
    CollectorGroupDto cg = new CollectorGroupDto();
    cg.setUuid(UUID.randomUUID());
    cg.setCollectingEvents(List.of(event));
    cg.setName("new test collector group");
    cg.setAgentIdentifiers(identifiers.toArray(new UUID[0]));
    CollectorGroupDto result = collectorGroupRepository.findOne(
      collectorGroupRepository.create(cg).getUuid(),
      new QuerySpec(CollectorGroupDto.class));
    assertNotNull(result.getCreatedBy());
    assertEquals(cg.getCollectingEvents().get(0).getUuid(), result.getCollectingEvents().get(0).getUuid());
  }

}
