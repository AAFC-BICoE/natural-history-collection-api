package ca.gc.aafc.collection.api.service;


import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.entities.SplitConfiguration;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;

@Service
public class SplitConfigurationService extends DefaultDinaService<SplitConfiguration> {

  public SplitConfigurationService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv
  ) {
    super(baseDAO, sv);
  }

  @Override
  protected void preCreate(SplitConfiguration entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
  }

}
