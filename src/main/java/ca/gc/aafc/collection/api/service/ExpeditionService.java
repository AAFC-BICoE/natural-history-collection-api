package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;

import ca.gc.aafc.collection.api.dto.ExpeditionDto;
import ca.gc.aafc.collection.api.entities.Expedition;
import ca.gc.aafc.collection.api.validation.ExpeditionValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.messaging.DinaEventPublisher;
import ca.gc.aafc.dina.messaging.EntityChanged;
import ca.gc.aafc.dina.service.MessageProducingService;
import ca.gc.aafc.dina.util.UUIDHelper;

import lombok.NonNull;

@Service
public class ExpeditionService extends MessageProducingService<Expedition> {

  private final ExpeditionValidator expeditionValidator;

  public ExpeditionService(
    @NonNull BaseDAO baseDAO,
    @NonNull SmartValidator sv,
    @NonNull ExpeditionValidator expeditionValidator,
    DinaEventPublisher<EntityChanged> eventPublisher
  ) {
    super(baseDAO, sv, ExpeditionDto.TYPENAME, eventPublisher);
    this.expeditionValidator = expeditionValidator;
  }

  @Override
  protected void preCreate(Expedition entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
    entity.setGroup(standardizeGroupName(entity));
  }

  @Override
  public void validateBusinessRules(Expedition entity) {
    applyBusinessRule(entity, expeditionValidator);
  }

}
