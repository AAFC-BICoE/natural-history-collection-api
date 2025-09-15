package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.config.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.dto.FieldExtensionValueDto;
import ca.gc.aafc.dina.service.CollectionBackedReadOnlyDinaService;
import java.util.stream.Collectors;

@Service
public class FieldExtensionValueService extends CollectionBackedReadOnlyDinaService<String, FieldExtensionValueDto> {
  
  public FieldExtensionValueService(CollectionExtensionConfiguration collectionExtensionConfiguration) {
      super(collectionExtensionConfiguration.getExtension().values().stream()
      .flatMap(extension -> extension.getFields().stream()
      .map(field -> new FieldExtensionValueDto(extension.getKey() + "." + field.getKey(), extension.getName(), extension.getKey(), field)))
      .collect(Collectors.toList()), FieldExtensionValueDto::getId);
  }
}
