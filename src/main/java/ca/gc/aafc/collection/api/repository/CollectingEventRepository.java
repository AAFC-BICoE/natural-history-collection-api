package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.datetime.IsoDateTimeRsqlResolver;
import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.service.CollectingEventService;
import ca.gc.aafc.dina.filter.DinaFilterResolver;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.AuditService;
import ca.gc.aafc.dina.service.DinaAuthorizationService;
import ca.gc.aafc.dina.service.GroupAuthorizationService;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public class CollectingEventRepository extends DinaRepository<CollectingEventDto, CollectingEvent> {

  private Optional<DinaAuthenticatedUser> authenticatedUser;  

    public CollectingEventRepository(
    @NonNull CollectingEventService dinaService,
    @NonNull GroupAuthorizationService groupAuthService,
    @NonNull AuditService auditService,
    @NonNull BuildProperties props,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull ExternalResourceProvider externalResourceProvider
  ) {
    super(
      dinaService,
      Optional.of(groupAuthService),
      Optional.of(auditService),
      new DinaMapper<>(CollectingEventDto.class),
      CollectingEventDto.class,
      CollectingEvent.class,
      new DinaFilterResolver(new IsoDateTimeRsqlResolver(Map.of(
        "startEventDateTime", "startEventDateTimePrecision",
        "endEventDateTime", "endEventDateTimePrecision"
      ))),
      externalResourceProvider,
      props);

    this.authenticatedUser = authenticatedUser;
  }

  @Override
  public <S extends CollectingEventDto> S create(S resource) {
    authenticatedUser.ifPresent(user -> resource.setCreatedBy(user.getUsername()));
    return super.create(resource);
  }
}

