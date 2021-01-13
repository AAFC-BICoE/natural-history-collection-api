package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.CollectorGroup;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

@Service
public class CollectorGroupService extends DefaultDinaService<CollectorGroup> {

    public CollectorGroupService(@NonNull BaseDAO baseDAO) {
        super(baseDAO);
    }

    @Override
    protected void preCreate(CollectorGroup entity) {    
      entity.setUuid(UUID.randomUUID());    
    }
    
}
