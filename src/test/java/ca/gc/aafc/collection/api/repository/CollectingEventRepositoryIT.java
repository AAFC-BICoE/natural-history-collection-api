package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.datetime.ISODateTime;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import io.crnk.core.queryspec.QuerySpec;

public class CollectingEventRepositoryIT extends CollectionModuleBaseIT{
    @Inject
    private CollectingEventRepository collectingEventRepository;
  
    private CollectingEvent testCollectingEvent;

    private static final LocalDate startDate = LocalDate.of(2000,1, 1);
    private static final LocalTime startTime = LocalTime.of(0,1);
  
    private static final LocalDate endDate = LocalDate.of(2002,10, 10);
    private static final LocalTime endTime = LocalTime.of(10,10);      

    private CollectingEvent createTestCollectingEvent() {
      testCollectingEvent = CollectingEventFactory.newCollectingEvent()
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
  
      service.save(testCollectingEvent);
      return testCollectingEvent;
    }
  
    @BeforeEach
    public void setup(){
      createTestCollectingEvent();
    }    

    @Test
    public void findCollectingEvent_whenNoFieldsAreSelected_CollectingEventReturnedWithAllFields() {
      CollectingEventDto collectingEventDto = collectingEventRepository
          .findOne(testCollectingEvent.getUuid(), new QuerySpec(CollectingEventDto.class));
      assertNotNull(collectingEventDto);
      assertEquals(testCollectingEvent.getUuid(), collectingEventDto.getUuid());
      assertEquals(testCollectingEvent.getCreatedBy(), collectingEventDto.getCreatedBy());
      assertEquals(testCollectingEvent.supplyStartISOEventDateTime().toString(), collectingEventDto.getStartEventDateTime());
      assertEquals(testCollectingEvent.supplyEndISOEventDateTime().toString(), collectingEventDto.getEndEventDateTime());
      assertEquals("XI-02-1798", collectingEventDto.getVerbatimEventDateTime());    
      assertEquals(26.089, collectingEventDto.getDecimalLatitude());    
      assertEquals(106.36, collectingEventDto.getDecimalLongitude());    
      assertEquals(208, collectingEventDto.getCoordinateUncertaintyInMeters());    
      assertEquals("26.089, 106.36", collectingEventDto.getVerbatimCoordinates());    

    }   
    
    @Test
    public void create_WithAuthenticatedUser_SetsCreatedBy() {
      CollectingEventDto ce = new CollectingEventDto();
      ce.setUuid(UUID.randomUUID());
      ce.setStartEventDateTime(ISODateTime.parse("2007-12-03T10:15:30").toString());
      ce.setEndEventDateTime(ISODateTime.parse("2007-12-04T11:20:20").toString());
      ce.setVerbatimCoordinates("26.089, 106.36");
      CollectingEventDto result = collectingEventRepository.findOne(collectingEventRepository.create(ce).getUuid(),
          new QuerySpec(CollectingEventDto.class));
      assertNotNull(result.getCreatedBy());
    }    
    
}
