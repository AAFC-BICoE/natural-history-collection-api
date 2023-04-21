package ca.gc.aafc.collection.api.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition;
import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Extension;
import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Field;
import ca.gc.aafc.dina.repository.NoLinkInformation;

import org.springframework.stereotype.Repository;

import ca.gc.aafc.collection.api.config.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.dto.ExtensionDto;
import ca.gc.aafc.collection.api.dto.FieldExtensionValueDto;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.DefaultResourceList;
import io.crnk.core.resource.list.ResourceList;
import io.crnk.core.resource.meta.DefaultPagedMetaInformation;
import lombok.NonNull;

@Repository
public class FieldExtensionValueRepository extends ReadOnlyResourceRepositoryBase<FieldExtensionValueDto, String> {

  private final List<ExtensionDto> extensions;
  public static final Pattern KEY_LOOKUP_PATTERN = Pattern.compile("(.*)\\.(.*)");

  protected FieldExtensionValueRepository(
      @NonNull CollectionExtensionConfiguration extensionConfiguration) {
    super(FieldExtensionValueDto.class);
    checkArguments(extensionConfiguration.getExtension());

    extensions = extensionConfiguration.getExtension()
        .entrySet()
        .stream()
        .map(entry -> new ExtensionDto(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());
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
  public ResourceList<FieldExtensionValueDto> findAll(QuerySpec querySpec) {
    System.out.println(querySpec);
    List<FieldExtensionValueDto> dtos = new ArrayList<FieldExtensionValueDto>();
    DefaultPagedMetaInformation meta = new DefaultPagedMetaInformation();
    return new DefaultResourceList<>(dtos, meta, new NoLinkInformation());
  }

  @Override
  public FieldExtensionValueDto findOne(String path, QuerySpec querySpec) {
    // Allow lookup by component extension key + field key.
    // e.g. mixs_soil_v4.alkalinity
    var matcher = KEY_LOOKUP_PATTERN.matcher(path.toString());
    String extensionName = "";
    Field field = null;
    if (matcher.groupCount() == 2) {
      if (matcher.find()) {
        String extensionKey = matcher.group(1);
        String fieldKey = matcher.group(2);
        for (ExtensionDto extensionDto : extensions) {
          Extension extension = extensionDto.getExtension();
          if (extension.getKey().equals(extensionKey)) {
            extensionName = extension.getName();
            field = extension.getFieldByKey(fieldKey);
          }
        }
      }

    }
    return new FieldExtensionValueDto(path, extensionName, field);
  }
}
