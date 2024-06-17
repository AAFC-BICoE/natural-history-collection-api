package ca.gc.aafc.collection.api.repository;

import java.util.Map;
import java.util.regex.Pattern;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Extension;

import org.springframework.stereotype.Repository;
import org.springframework.web.server.MethodNotAllowedException;

import ca.gc.aafc.collection.api.config.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.dto.FieldExtensionValueDto;
import ca.gc.aafc.dina.security.TextHtmlSanitizer;

import io.crnk.core.exception.ResourceNotFoundException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ReadOnlyResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import lombok.NonNull;

// CHECKSTYLE:OFF NoFinalizer
// CHECKSTYLE:OFF SuperFinalize
@Repository
public class FieldExtensionValueRepository extends ReadOnlyResourceRepositoryBase<FieldExtensionValueDto, String> {

  private final Map<String, Extension> extensions;

  public static final Pattern KEY_LOOKUP_PATTERN = Pattern.compile("(.*)\\.(.*)");

  protected FieldExtensionValueRepository(
      @NonNull CollectionExtensionConfiguration extensionConfiguration) {
    super(FieldExtensionValueDto.class);
    extensions = extensionConfiguration.getExtension();
  }

  @Override
  public ResourceList<FieldExtensionValueDto> findAll(QuerySpec querySpec) {
    throw new MethodNotAllowedException("findAll", null);
  }

  @Override
  public FieldExtensionValueDto findOne(String path, QuerySpec querySpec) {
    // Allow lookup by component extension key + field key.
    // e.g. mixs_soil_v4.alkalinity
    var matcher = KEY_LOOKUP_PATTERN.matcher(path);
    if (matcher.groupCount() == 2) {
      if (matcher.find()) {
        String extensionKey = matcher.group(1);
        String fieldKey = matcher.group(2);

        Extension ext = extensions.get(extensionKey);
        if (ext != null && ext.getFieldByKey(fieldKey) != null) {
          return new FieldExtensionValueDto(path, ext.getName(), extensionKey,
            ext.getFieldByKey(fieldKey));
        }
      }
    }
    throw new ResourceNotFoundException("Field Extension Value not found: " + TextHtmlSanitizer.sanitizeText(path));
  }

  protected final void finalize() {
    // no-op
  }
}
