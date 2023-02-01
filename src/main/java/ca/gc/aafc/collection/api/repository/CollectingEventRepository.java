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
import ca.gc.aafc.dina.security.DinaAuthorizationService;
import ca.gc.aafc.dina.security.TextHtmlSanitizer;
import ca.gc.aafc.dina.service.AuditService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Repository
public class CollectingEventRepository extends DinaRepository<CollectingEventDto, CollectingEvent> {

  private Optional<DinaAuthenticatedUser> authenticatedUser;  

  public CollectingEventRepository(
    @NonNull CollectingEventService dinaService,
    DinaAuthorizationService groupAuthorizationService,
    @NonNull AuditService auditService,
    @NonNull BuildProperties props,
    Optional<DinaAuthenticatedUser> authenticatedUser,
    @NonNull ExternalResourceProvider externalResourceProvider,
    ObjectMapper objectMapper
  ) {
    super(
      dinaService,
      groupAuthorizationService,
      Optional.of(auditService),
      new DinaMapper<>(CollectingEventDto.class),
      CollectingEventDto.class,
      CollectingEvent.class,
      new DinaFilterResolver(new IsoDateTimeRsqlResolver(Map.of(
        "startEventDateTime", "startEventDateTimePrecision",
        "endEventDateTime", "endEventDateTimePrecision"
      ))),
      externalResourceProvider,
      props, objectMapper);

    this.authenticatedUser = authenticatedUser;
  }

  @Override
  public <S extends CollectingEventDto> S create(S resource) {
    authenticatedUser.ifPresent(user -> resource.setCreatedBy(user.getUsername()));
    return super.create(resource);
  }

  @Override
  protected Predicate<String> supplyPredicate() {
    return txt -> TextHtmlSanitizer.isSafeText(txt) || TextHtmlSanitizer.isAcceptableText(txt);
  }

}

