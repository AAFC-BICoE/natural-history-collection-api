package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;

@Service
public class SiteService extends DefaultDinaService<Site> {

    public SiteService(@NonNull BaseDAO baseDAO) {
        super(baseDAO);
    }

    @Override
    protected void preCreate(Site entity) {    
      entity.setUuid(UUID.randomUUID());    
    }    
}
