package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.CollectingEventManagedAttribute;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;


@Service
public class CollectingEventManagedAttributeService extends DefaultDinaService<CollectingEventManagedAttribute> {
    
    public CollectingEventManagedAttributeService(@NonNull BaseDAO baseDAO) {
        super(baseDAO);
    }
    
    @Override
    protected void preCreate(CollectingEventManagedAttribute entity) {
        entity.setUuid(UUID.randomUUID());
    }
}
