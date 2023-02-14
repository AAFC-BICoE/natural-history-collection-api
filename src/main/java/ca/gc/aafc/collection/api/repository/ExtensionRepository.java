package ca.gc.aafc.collection.api.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition;
import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.config.CollectionExtensionConfiguration;
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
    checkArguments(extensionConfiguration.getExtension());

    extension = extensionConfiguration.getExtension()
      .entrySet()
      .stream()
      .map( entry -> new ExtensionDto(entry.getKey(), entry.getValue()))
      .collect( Collectors.toList());
  }

  /**
   * Very common configuration issue that could lead to errors.
   *
   * @param extension
   */
  private void checkArguments(Map<String, FieldExtensionDefinition.Extension> extension) {
    for (var entry : extension.entrySet()) {
      if (!entry.getKey().equals(entry.getValue().getKey())) {
        throw new IllegalStateException("Extension map key not matching extension key: "
                + entry.getKey() + " vs " + entry.getValue().getKey());
      }
    }
  }

  @Override
  public ResourceList<ExtensionDto> findAll(QuerySpec querySpec) {
    return querySpec.apply(extension);
  }
  
}
