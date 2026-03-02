package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;
import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.messaging.DinaEventPublisher;
import ca.gc.aafc.dina.messaging.EntityChanged;
import ca.gc.aafc.dina.service.MessageProducingService;
import ca.gc.aafc.dina.util.UUIDHelper;
import lombok.NonNull;

@Service
public class SiteService extends MessageProducingService<Site> {

  public SiteService(
      @NonNull BaseDAO baseDAO,
      @NonNull SmartValidator sv,
      DinaEventPublisher<EntityChanged> eventPublisher) {
    super(baseDAO, sv, SiteDto.TYPENAME, eventPublisher);
  }

  @Override
  protected void preCreate(Site entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
    entity.setGroup(standardizeGroupName(entity));
  }
}
