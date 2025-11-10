package ca.gc.aafc.collection.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;

import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.repository.CollectingEventRepository;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.dina.datetime.ISODateTime;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import javax.inject.Inject;

@SpringBootTest(properties = "keycloak.enabled=true")
public class CollectingEventAuthorisationIT extends CollectionModuleBaseIT {

  @Inject
  private CollectingEventRepository collectingEventRepository;

  private CollectingEvent testCollectingEvent;

  private static final LocalDate startDate = LocalDate.of(2000, 1, 1);
  private static final LocalTime startTime = LocalTime.of(0, 1);

  private static final LocalDate endDate = LocalDate.of(2002, 10, 10);
  private static final LocalTime endTime = LocalTime.of(10, 10);  

  @WithMockKeycloakUser(groupRole = { "amf:user" })
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

  @WithMockKeycloakUser(groupRole = { "aafc:SUPER_USER" })
  @Test
  public void when_UpdatingAsUserFromGroupOtherThanEventGroup_AccessDenied()
      throws ResourceGoneException, ResourceNotFoundException {
    CollectingEventDto retrievedEvent = collectingEventRepository.getOne(testCollectingEvent.getUuid(), "").getDto();
    retrievedEvent.setDwcVerbatimDepth("10-20m");

    JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
      testCollectingEvent.getUuid(), CollectingEventDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(retrievedEvent)
    );

    assertThrows(AccessDeniedException.class,()-> collectingEventRepository.onUpdate(docToUpdate,testCollectingEvent.getUuid()));
  }

  @WithMockKeycloakUser(groupRole = { "amf:USER" })
  @Test
  public void when_UpdatingAsUserFromEventGroup_EventUpdated()
      throws ResourceGoneException, ResourceNotFoundException {
    CollectingEventDto retrievedEventDto = collectingEventRepository.getOne(testCollectingEvent.getUuid(), "").getDto();
    retrievedEventDto.setDwcVerbatimDepth("10-20m");

    JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
      testCollectingEvent.getUuid(), CollectingEventDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(retrievedEventDto)
    );

    collectingEventRepository.onUpdate(docToUpdate, testCollectingEvent.getUuid());
    CollectingEvent updatedEvent = collectingEventService.findOne(retrievedEventDto.getUuid(), CollectingEvent.class);
    assertEquals("10-20m", updatedEvent.getDwcVerbatimDepth());
  }

  @WithMockKeycloakUser(groupRole = { "aafc:SUPER_USER" })
  @Test
  public void when_deleteAsUserFromGroupOtherThanEventGroup_AccessDenied()
      throws ResourceGoneException, ResourceNotFoundException {
    CollectingEventDto retrievedEventDto = collectingEventRepository.getOne(testCollectingEvent.getUuid(), "").getDto();
    assertThrows(AccessDeniedException.class,()-> collectingEventRepository.delete(retrievedEventDto.getUuid()));
    assertNotNull(collectingEventService.findOne(testCollectingEvent.getUuid(), CollectingEvent.class));
  }

  @WithMockKeycloakUser(groupRole = { "amf:USER" })
  @Test
  public void when_deleteAsUserFromEventGroup_EventDeleted()
      throws ResourceGoneException, ResourceNotFoundException {
    CollectingEventDto ce = new CollectingEventDto();
    ce.setGroup("amf");
    ce.setUuid(null);
    ce.setStartEventDateTime(ISODateTime.parse("2007-12-03T10:15:30").toString());

    JsonApiDocument docToCreate = JsonApiDocuments.createJsonApiDocument(
      null, CollectingEventDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(ce)
    );

    UUID ceUUID = JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(collectingEventRepository.onCreate(docToCreate));
    CollectingEventDto retrievedEvent = collectingEventRepository.getOne(ceUUID, "").getDto();
    assertNotNull(retrievedEvent.getUuid());
    collectingEventRepository.delete(retrievedEvent.getUuid());
    assertThrows(ResourceGoneException.class, ()-> collectingEventRepository.getOne(ceUUID, ""));
  }
    
}
