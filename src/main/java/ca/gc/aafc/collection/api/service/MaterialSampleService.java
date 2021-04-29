package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

@Service
public class MaterialSampleService extends DefaultDinaService<MaterialSample> {

    public MaterialSampleService(@NonNull BaseDAO baseDAO) {
        super(baseDAO);
    }
    
    @Override
    protected void preCreate(MaterialSample entity) {
        entity.setUuid(UUID.randomUUID());
    }
}