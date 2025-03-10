package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.collection.api.validation.ProtocolValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;
import org.springframework.validation.SmartValidator;

@Service
public class ProtocolService extends DefaultDinaService<Protocol> {

  private final ProtocolValidator protocolValidator;

  public ProtocolService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv, ProtocolValidator protocolValidator) {
    super(baseDAO, sv);
    this.protocolValidator = protocolValidator;
  }

  @Override
  protected void preCreate(Protocol entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
    entity.setGroup(standardizeGroupName(entity));
  }

  @Override
  public void validateBusinessRules(Protocol entity) {
    applyBusinessRule(entity, protocolValidator);
  }
}
