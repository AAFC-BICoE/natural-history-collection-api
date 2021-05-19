package ca.gc.aafc.collection.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.validation.MaterialSampleValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

@Service
public class MaterialSampleService extends DefaultDinaService<MaterialSample> {

    private final MaterialSampleValidator materialSampleValidator;

    public MaterialSampleService(
        @NonNull BaseDAO baseDAO,
        @NonNull MaterialSampleValidator materialSampleValidator) {
        super(baseDAO);
        this.materialSampleValidator = materialSampleValidator;
    }
    
    @Override
    protected void preCreate(MaterialSample entity) {
        entity.setUuid(UUID.randomUUID());
        validateMaterialSample(entity);
    }

    @Override
    protected void preUpdate(MaterialSample entity) {
        validateMaterialSample(entity);
    }

    public void validateMaterialSample(MaterialSample entity) {
        validateBusinessRules(entity, materialSampleValidator);
    }
}
