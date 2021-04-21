package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.ManagedAttributeDto;
import ca.gc.aafc.collection.api.entities.ManagedAttribute;
import ca.gc.aafc.collection.api.service.ManagedAttributeService;
import ca.gc.aafc.dina.mapper.DinaMapper;
import ca.gc.aafc.dina.repository.DinaRepository;
import ca.gc.aafc.dina.repository.external.ExternalResourceProvider;
import ca.gc.aafc.dina.security.DinaAuthenticatedUser;
import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.FilterOperator;
import io.crnk.core.queryspec.FilterSpec;
import io.crnk.core.queryspec.QuerySpec;
import lombok.NonNull;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Repository
public class ManagedAttributeRepo extends DinaRepository<ManagedAttributeDto, ManagedAttribute> {

  private Optional<DinaAuthenticatedUser> dinaAuthenticatedUser;

  public static final Pattern KEY_LOOKUP_PATTERN = Pattern.compile("(.*)\\.(.*)");

  public ManagedAttributeRepo(
    @NonNull ManagedAttributeService service,
    ExternalResourceProvider externalResourceProvider,
    @NonNull BuildProperties buildProperties,
    Optional<DinaAuthenticatedUser> dinaAuthenticatedUser
  ) {
    super(
      service,
      Optional.empty(),
      Optional.empty(),
      new DinaMapper<>(ManagedAttributeDto.class),
      ManagedAttributeDto.class,
      ManagedAttribute.class,
      null,
      externalResourceProvider,
      buildProperties);
    this.dinaAuthenticatedUser = dinaAuthenticatedUser;
  }

  @Override
  public <S extends ManagedAttributeDto> S create(S resource) {
    dinaAuthenticatedUser.ifPresent(
      authenticatedUser -> resource.setCreatedBy(authenticatedUser.getUsername()));
    return super.create(resource);
  }

  @Override
  public ManagedAttributeDto findOne(Serializable id, QuerySpec querySpec) {

    // Allow lookup by component type + key.
    // e.g. collecting_event.attribute_name
    var matcher = KEY_LOOKUP_PATTERN.matcher(id.toString());
    if (matcher.groupCount() == 2) {
      if (matcher.find()) {
        String componentType = matcher.group(1).toUpperCase();
        String attributeKey = matcher.group(2);

        QuerySpec keyQuerySpec = new QuerySpec(ManagedAttributeDto.class);
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
