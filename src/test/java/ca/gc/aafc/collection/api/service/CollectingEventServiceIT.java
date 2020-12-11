package ca.gc.aafc.collection.api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;

public class CollectingEventServiceIT extends CollectionModuleBaseIT{ 

    
  @Inject
  private CollectingEventService collectingEventService;  

  private static final LocalDate startDate = LocalDate.of(2000,1, 1);
  private static final LocalTime startTime = LocalTime.of(0,1);

  private static final LocalDate endDate = LocalDate.of(2002,10, 10);
  private static final LocalTime endTime = LocalTime.of(10,10);  
  
  @Test
  public void collectingEventService_OnCreate_CorrectStartAndEndDatetimeSet() {   
    CollectingEvent newEvent = CollectingEventFactory.newCollectingEvent()
    .startEventDateTime(LocalDateTime.of(startDate, startTime))
    .startEventDateTimePrecision((byte) 8)
    .endEventDateTime(LocalDateTime.of(endDate, endTime))
    .endEventDateTimePrecision((byte) 8)
    .verbatimEventDateTime("XI-02-1798")
    .decimalLatitude(26.089)
    .decimalLongitude(106.36)
    .coordinateUncertaintyInMeters(208)
    .verbatimCoordinates("26.089, 106.36")
    .build();
    CollectingEvent newEventCreated =  collectingEventService.create(newEvent);
    assertEquals(newEvent.getStartEventDateTime(), newEventCreated.getStartEventDateTime());
    assertEquals(newEvent.getStartEventDateTimePrecision(), newEventCreated.getStartEventDateTimePrecision());
    assertEquals(newEvent.getEndEventDateTime(), newEventCreated.getEndEventDateTime());
    assertEquals(newEvent.getEndEventDateTimePrecision(), newEventCreated.getEndEventDateTimePrecision());    
    assertEquals(newEvent.getVerbatimEventDateTime(), newEventCreated.getVerbatimEventDateTime());    
    assertEquals(newEvent.getDecimalLatitude(), newEventCreated.getDecimalLatitude());    
    assertEquals(newEvent.getDecimalLongitude(), newEventCreated.getDecimalLongitude());    
    assertEquals(newEvent.getCoordinateUncertaintyInMeters(), newEventCreated.getCoordinateUncertaintyInMeters());    
    assertEquals(newEvent.getVerbatimCoordinates(), newEventCreated.getVerbatimCoordinates());    
  }  

}