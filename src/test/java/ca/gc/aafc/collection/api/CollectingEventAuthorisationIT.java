package ca.gc.aafc.collection.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import javax.inject.Inject;

import ca.gc.aafc.dina.repository.GoneException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.repository.CollectingEventRepository;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectingEventAuthorisationIT extends CollectionModuleBaseIT {

  @Inject
  private CollectingEventRepository collectingEventRepository;

  private CollectingEvent testCollectingEvent;

  private static final LocalDate startDate = LocalDate.of(2000, 1, 1);
  private static final LocalTime startTime = LocalTime.of(0, 1);

  private static final LocalDate endDate = LocalDate.of(2002, 10, 10);
  private static final LocalTime endTime = LocalTime.of(10, 10);  

  @WithMockKeycloakUser(groupRole = { "amf: staff" })
  @BeforeEach
  public void setup() {
    createTestCollectingEvent();
  }

  private CollectingEvent createTestCollectingEvent() {
    testCollectingEvent = CollectingEventFactory.newCollectingEvent()
     .startEventDateTime(LocalDateTime.of(startDate, startTime)).startEventDateTimePrecision((byte) 8)
     .endEventDateTime(LocalDateTime.of(endDate, endTime)).endEventDateTimePrecision((byte) 8)
     .verbatimEventDateTime("XI-02-1798").uuid(UUID.randomUUID()).build();

    testCollectingEvent.setGroup("amf");

    collectingEventService.create(testCollectingEvent);

    return testCollectingEvent;
  }

  @WithMockKeycloakUser(groupRole = { "aafc: collection-manager" })
  @Test
  public void when_UpdatingAsUserFromGroupOtherthanEventGroup_AccessDenied() {
    CollectingEventDto retrievedEvent = collectingEventRepository.findOne(testCollectingEvent.getUuid(),
        new QuerySpec(CollectingEventDto.class));
    retrievedEvent.setDwcVerbatimDepth("10-20m");        
    Assertions.assertThrows(AccessDeniedException.class,()-> collectingEventRepository.save(retrievedEvent));    
  }

  @WithMockKeycloakUser(groupRole = { "amf: staff" })  
  @Test
  public void when_UpdatingAsUserFromEventGroup_EventUpdated(){
    CollectingEventDto retrievedEventDto = collectingEventRepository.findOne(testCollectingEvent.getUuid(),
        new QuerySpec(CollectingEventDto.class));
    retrievedEventDto.setDwcVerbatimDepth("10-20m");        
    collectingEventRepository.save(retrievedEventDto);
    CollectingEvent updatedEvent = collectingEventService.findOne(retrievedEventDto.getUuid(), CollectingEvent.class);
    assertEquals("10-20m", updatedEvent.getDwcVerbatimDepth());       
  }

  @WithMockKeycloakUser(groupRole = { "aafc: collection-manager" })
  @Test
  public void when_deleteAsUserFromGroupOtherthanEventGroup_AccessDenied() {
    CollectingEventDto retrievedEvent = collectingEventRepository.findOne(testCollectingEvent.getUuid(),
        new QuerySpec(CollectingEventDto.class));
    assertNotNull(collectingEventService.findOne(testCollectingEvent.getUuid(), CollectingEvent.class));
    Assertions.assertThrows(AccessDeniedException.class,()-> collectingEventRepository.delete(retrievedEvent.getUuid()));    
    assertNotNull(collectingEventService.findOne(testCollectingEvent.getUuid(), CollectingEvent.class));
  }

  @WithMockKeycloakUser(groupRole = { "amf: staff" })
  @Test
  public void when_deleteAsUserFromEventGroup_EventDeleted(){
    CollectingEventDto ce = new CollectingEventDto();
    ce.setGroup("amf");
    ce.setUuid(null);
    ce.setStartEventDateTime(ISODateTime.parse("2007-12-03T10:15:30").toString());    
    CollectingEventDto retrievedEvent = collectingEventRepository.findOne(
      collectingEventRepository.create(ce).getUuid(),
      new QuerySpec(CollectingEventDto.class));

    assertNotNull(retrievedEvent.getUuid());
    collectingEventRepository.delete(retrievedEvent.getUuid());        
    Assertions.assertThrows(GoneException.class, ()->collectingEventRepository.findOne(retrievedEvent.getUuid(),
         new QuerySpec(CollectingEventDto.class)));    

  }
    
}
