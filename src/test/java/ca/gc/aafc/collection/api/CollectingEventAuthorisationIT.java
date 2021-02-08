package ca.gc.aafc.collection.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

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
     .verbatimEventDateTime("XI-02-1798").build();

     testCollectingEvent.setGroup("amf");

    service.save(testCollectingEvent);
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
    CollectingEvent updatedEvent = service.findUnique(CollectingEvent.class, "uuid", retrievedEventDto.getUuid());
    assertEquals("10-20m", updatedEvent.getDwcVerbatimDepth());       
  }

  @WithMockKeycloakUser(groupRole = { "aafc: collection-manager" })
  @Test
  public void when_deleteAsUserFromGroupOtherthanEventGroup_AccessDenied() {
    CollectingEventDto retrievedEvent = collectingEventRepository.findOne(testCollectingEvent.getUuid(),
        new QuerySpec(CollectingEventDto.class));
    assertNotNull(service.find(CollectingEvent.class, testCollectingEvent.getId()));
    Assertions.assertThrows(AccessDeniedException.class,()-> collectingEventRepository.delete(retrievedEvent.getUuid()));    
    assertNotNull(service.find(CollectingEvent.class, testCollectingEvent.getId()));
  }

  @WithMockKeycloakUser(groupRole = { "amf: staff" })
  @Test
  public void when_deleteAsUserFromEventGroup_Eventdeleted(){
    CollectingEventDto retrievedEvent = collectingEventRepository.findOne(testCollectingEvent.getUuid(),
        new QuerySpec(CollectingEventDto.class));
    assertNotNull(service.find(CollectingEvent.class, testCollectingEvent.getId()));
    collectingEventRepository.delete(retrievedEvent.getUuid());    
    assertNull(service.find(CollectingEvent.class, testCollectingEvent.getId()));
  }
    
}
