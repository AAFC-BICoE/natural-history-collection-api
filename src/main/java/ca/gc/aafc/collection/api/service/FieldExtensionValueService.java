package ca.gc.aafc.collection.api.service;

import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.config.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.dto.FieldExtensionValueDto;
import ca.gc.aafc.dina.service.CollectionBackedReadOnlyDinaService;
import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Extension;

import java.util.stream.Collectors;

@Service
public class FieldExtensionValueService extends CollectionBackedReadOnlyDinaService<String, FieldExtensionValueDto> {
//
//  private final Map<String, Extension> extensions;
//  public static final Pattern KEY_LOOKUP_PATTERN = Pattern.compile("(.*)\\.(.*)");

  public FieldExtensionValueService(CollectionExtensionConfiguration collectionExtensionConfiguration) {
    super(collectionExtensionConfiguration.getExtension()
      .entrySet()
      .stream()
      .map(entry -> new FieldExtensionValueDto(entry.getKey() + "." + entry.getValue().getKey() , entry.getValue().getName(), entry.getValue().getKey(),
          entry.getValue().getFieldByKey(entry.getValue().getKey())))
      .collect(Collectors.toList()), FieldExtensionValueDto::getId);

 //   extensions = collectionExtensionConfiguration.getExtension();
  }
//
//  @Override
//  public FieldExtensionValueDto findOne(String key) {
//    // Allow lookup by component extension key + field key.
//    // e.g. mixs_soil_v4.alkalinity
//    var matcher = KEY_LOOKUP_PATTERN.matcher(key);
//    if (matcher.groupCount() == 2) {
//      if (matcher.find()) {
//        String extensionKey = matcher.group(1);
//        String fieldKey = matcher.group(2);
//
//        Extension ext = extensions.get(extensionKey);
//        if (ext != null && ext.getFieldByKey(fieldKey) != null) {
//          return new FieldExtensionValueDto(key, ext.getName(), extensionKey,
//            ext.getFieldByKey(fieldKey));
//        }
//      }
//    }
//    return null;
//  }
}
