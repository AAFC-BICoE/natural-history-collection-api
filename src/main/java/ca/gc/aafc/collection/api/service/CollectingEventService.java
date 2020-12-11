package ca.gc.aafc.collection.api.service;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;

@Service
public class CollectingEventService extends DefaultDinaService<CollectingEvent>{
  public CollectingEventService(@NotNull BaseDAO baseDAO){
    super(baseDAO);
  }
    
}
