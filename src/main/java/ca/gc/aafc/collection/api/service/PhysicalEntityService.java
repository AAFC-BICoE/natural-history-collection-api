package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.PhysicalEntity;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

@Service
public class PhysicalEntityService extends DefaultDinaService<PhysicalEntity> {

    public PhysicalEntityService(@NonNull BaseDAO baseDAO) {
        super(baseDAO);
    }
    
    @Override
    protected void preCreate(PhysicalEntity entity) {
        entity.setUuid(UUID.randomUUID());
    }
}
