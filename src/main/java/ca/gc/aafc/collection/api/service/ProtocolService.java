package ca.gc.aafc.collection.api.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.service.DefaultDinaService;
import lombok.NonNull;
import org.springframework.validation.SmartValidator;

@Service
public class Protocol extends DefaultDinaService<Protocol> {
  public ProtocolService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv) {
    super(baseDAO, sv);
  }

  @Override
  protected void preCreate(Protocol entity) {
    entity.setUuid(UUID.randomUUID());
  }
  
}
