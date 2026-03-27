package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;
import org.springframework.validation.SmartValidator;
import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.collection.api.entities.Site;
import ca.gc.aafc.collection.api.validation.CollectionManagedAttributeValueValidatorSite;
import ca.gc.aafc.dina.jpa.BaseDAO;
import ca.gc.aafc.dina.messaging.DinaEventPublisher;
import ca.gc.aafc.dina.messaging.EntityChanged;
import ca.gc.aafc.dina.messaging.message.DocumentOperationType;
import ca.gc.aafc.dina.service.MessageProducingService;
import ca.gc.aafc.dina.util.UUIDHelper;

import java.util.EnumSet;
import lombok.NonNull;

@Service
public class SiteService extends MessageProducingService<Site> {
  private final CollectionManagedAttributeValueValidatorSite managedAttributeValueValidator;

  public SiteService(@NonNull BaseDAO baseDAO, @NonNull SmartValidator sv,
      @NonNull CollectionManagedAttributeValueValidatorSite managedAttributeValueValidator,
      DinaEventPublisher<EntityChanged> eventPublisher) {
    super(baseDAO, sv, SiteDto.TYPENAME,
        EnumSet.of(DocumentOperationType.UPDATE, DocumentOperationType.DELETE),
        eventPublisher);

    this.managedAttributeValueValidator = managedAttributeValueValidator;

  }

  @Override
  protected void preCreate(Site entity) {
    entity.setUuid(UUIDHelper.generateUUIDv7());
    entity.setGroup(standardizeGroupName(entity));
    cleanupManagedAttributes(entity);
  }

  @Override
  public void preUpdate(Site entity) {
    cleanupManagedAttributes(entity);
  }

  @Override
  public void validateBusinessRules(Site entity) {
    validateManagedAttribute(entity);
  }

  private void validateManagedAttribute(Site entity) {
    managedAttributeValueValidator.validate(
        entity,
        entity.getManagedAttributes());
  }

  private void cleanupManagedAttributes(Site entity) {
    var values = entity.getManagedAttributes();
    if (values == null) {
      return;
    }

    values.entrySet().removeIf(entry -> entry.getValue() == null || entry.getValue().isBlank());
  }

}
