package ca.gc.aafc.collection.api.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.CollectionManagedAttribute;
import ca.gc.aafc.collection.api.security.CollectionManagedAttributeAuthorizationService;
import ca.gc.aafc.collection.api.service.CollectionManagedAttributeService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;

import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.QuerySpec;
import lombok.NonNull;

@Repository
public class CollectionManagedAttributeRepo extends DinaRepository<CollectionManagedAttributeDto, CollectionManagedAttribute> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;

  public static final Pattern KEY_LOOKUP_PATTERN = Pattern.compile("(.*)\\.(.*)");

  public CollectionManagedAttributeRepo(
    @NonNull CollectionManagedAttributeService service,
    @NonNull CollectionManagedAttributeAuthorizationService collectionManagedAttributeAuthorizationService,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
    super(
      service,
      collectionManagedAttributeAuthorizationService,
      Optional.empty(),
      new DinaMapper<>(CollectionManagedAttributeDto.class),
      CollectionManagedAttributeDto.class,
      CollectionManagedAttribute.class,
      null,
      externalResourceProvider,
      buildProperties);
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
        String componentType = matcher.group(1).toUpperCase();
        String attributeKey = matcher.group(2);

        QuerySpec keyQuerySpec = new QuerySpec(CollectionManagedAttributeDto.class);
        keyQuerySpec.addFilter(
          new FilterSpec(List.of("key"), FilterOperator.EQ, attributeKey));
        keyQuerySpec.addFilter(
          new FilterSpec(List.of("managedAttributeComponent"), FilterOperator.EQ, componentType));
        
        var results = super.findAll(keyQuerySpec);
        if (results.size() > 0) {
          return results.get(0);
        } else {
          throw new ResourceNotFoundException("Managed Attribute not found: " + id);
        }
      }
    }

    return super.findOne(id, querySpec);
  }

}
