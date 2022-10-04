package ca.gc.aafc.collection.api.repository;

import java.io.Serializable;
import java.util.Optional;
import java.util.regex.Pattern;

import ca.gc.aafc.collection.api.security.SuperUserInGroupCUDAuthorizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.service.CollectionManagedAttributeService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import ca.gc.aafc.dina.service.AuditService;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import lombok.NonNull;

@Repository
public class CollectionManagedAttributeRepo extends DinaRepository<CollectionManagedAttributeDto, CollectionManagedAttribute> {

  private final CollectionManagedAttributeService dinaService;
  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;

  public static final Pattern KEY_LOOKUP_PATTERN = Pattern.compile("(.*)\\.(.*)");

  public CollectionManagedAttributeRepo(
    @NonNull CollectionManagedAttributeService service,
    @NonNull SuperUserInGroupCUDAuthorizationService authService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull AuditService auditService,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser,
    ObjectMapper objectMapper
  ) {
    super(
      service, authService,
      Optional.of(auditService),
      new DinaMapper<>(CollectionManagedAttributeDto.class),
      CollectionManagedAttributeDto.class,
      CollectionManagedAttribute.class,
      null,
      externalResourceProvider,
      buildProperties, objectMapper);
    this.dinaService = service;
    this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends CollectionManagedAttributeDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }

  @Override
  public CollectionManagedAttributeDto findOne(Serializable id, QuerySpec querySpec) {

    // Allow lookup by component type + key.
    // e.g. collecting_event.attribute_name
    var matcher = KEY_LOOKUP_PATTERN.matcher(id.toString());
    if (matcher.groupCount() == 2) {
      if (matcher.find()) {
        CollectionManagedAttribute.ManagedAttributeComponent component = CollectionManagedAttribute.ManagedAttributeComponent
            .fromString(matcher.group(1));
        String attributeKey = matcher.group(2);

        CollectionManagedAttribute managedAttribute =
            dinaService.findOneByKeyAndComponent(attributeKey, component);

        if (managedAttribute != null) {
          return getMappingLayer().toDtoSimpleMapping(managedAttribute);
        } else {
          throw new ResourceNotFoundException("Managed Attribute not found: " + id);
        }
      }
    }

    return super.findOne(id, querySpec);
  }

}
