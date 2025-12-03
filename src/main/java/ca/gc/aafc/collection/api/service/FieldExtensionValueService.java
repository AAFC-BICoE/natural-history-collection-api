package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.config.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.dto.FieldExtensionValueDto;
import ca.gc.aafc.dina.service.CollectionBackedReadOnlyDinaService;
import java.util.stream.Collectors;

@Service
public class FieldExtensionValueService extends CollectionBackedReadOnlyDinaService<String, FieldExtensionValueDto> {
  
  public FieldExtensionValueService(CollectionExtensionConfiguration collectionExtensionConfiguration) {
    super(collectionExtensionConfiguration.getExtension().values().stream() //get all extentions
      .flatMap(extension -> extension.getFields().stream() //extract nested field value lists into flat structure
      .map(field -> new FieldExtensionValueDto(extension.getKey() + "." + field.getKey(), extension.getName(), extension.getKey(), field))) //convert to DTO
      .collect(Collectors.toList()), FieldExtensionValueDto::getId);
  }
}
