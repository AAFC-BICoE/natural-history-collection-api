package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.GeoreferenceAssertion;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

@Service
public class GeoReferenceAssertionService extends DefaultDinaService<GeoreferenceAssertion> {

    public GeoReferenceAssertionService(@NonNull BaseDAO baseDAO) {
        super(baseDAO);
    }

    @Override
    protected void preCreate(GeoreferenceAssertion entity) {    
      entity.setUuid(UUID.randomUUID());    
    }
    
}
