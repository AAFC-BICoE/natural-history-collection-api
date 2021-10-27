package ca.gc.aafc.collection.api.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.dto.ExtensionDto;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import lombok.NonNull;

@Repository
public class ExtensionRepository extends ReadOnlyResourceRepositoryBase<ExtensionDto, String> {

  private final List<ExtensionDto> extension;

  protected ExtensionRepository(
    @NonNull CollectionExtensionConfiguration extensionConfiguration) {
    super(ExtensionDto.class);

    extension = extensionConfiguration.getExtension()
      .entrySet()
      .stream()
      .map( entry -> new ExtensionDto(entry.getKey(), entry.getValue()))
      .collect( Collectors.toList());
  }

  @Override
  public ResourceList<ExtensionDto> findAll(QuerySpec querySpec) {
    return querySpec.apply(extension);
  }
  
}
