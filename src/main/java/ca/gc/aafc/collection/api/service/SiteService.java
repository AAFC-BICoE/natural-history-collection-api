package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;
import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.collection.api.validation.SiteValidator;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.messaging.DinaEventPublisher;
import ca.gc.aafc.dina.messaging.EntityChanged;
import ca.gc.aafc.dina.service.MessageProducingService;
import ca.gc.aafc.dina.util.UUIDHelper;
import lombok.NonNull;

@Service
public class SiteService extends MessageProducingService<Site> {
  private SiteValidator siteValidator;

  public SiteService(
      @NonNull BaseDAO baseDAO,
      @NonNull SmartValidator sv,
      @NonNull SiteValidator siteValidator,
      DinaEventPublisher<EntityChanged> eventPublisher) {
    super(baseDAO, sv, SiteDto.TYPENAME, eventPublisher);
    this.siteValidator = siteValidator;
  }

  @Override
  protected void preCreate(Site entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
    entity.setGroup(standardizeGroupName(entity));
  }

  @Override
  public void validateBusinessRules(Site entity) {
    applyBusinessRule(entity, siteValidator);
  }
}
