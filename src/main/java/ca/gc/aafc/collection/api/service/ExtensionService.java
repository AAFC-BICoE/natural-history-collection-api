package ca.gc.aafc.collection.api.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.config.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.dto.ExtensionDto;
import ca.gc.aafc.dina.service.CollectionBackedReadOnlyDinaService;

@Service
public class ExtensionService extends CollectionBackedReadOnlyDinaService<String, ExtensionDto> {

  public ExtensionService(
    CollectionExtensionConfiguration extensionConfiguration) {

    super(extensionConfiguration.getExtension()
      .entrySet()
      .stream()
      .map( entry -> new ExtensionDto(entry.getKey(), entry.getValue()))
      .collect( Collectors.toList()), ExtensionDto::getId);
  }
}

