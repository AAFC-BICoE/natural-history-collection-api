package ca.gc.aafc.collection.api.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.config.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.dto.ExtensionDto;
import ca.gc.aafc.dina.extension.FieldExtensionDefinition;
import ca.gc.aafc.dina.service.CollectionBackedReadOnlyDinaService;

@Service
public class ExtensionService extends CollectionBackedReadOnlyDinaService<String, ExtensionDto> {

  public ExtensionService(CollectionExtensionConfiguration extensionConfiguration) {
    super(configToList(extensionConfiguration), ExtensionDto::getId);
  }

  private static List<ExtensionDto> configToList(CollectionExtensionConfiguration extensionConfiguration) {
    checkArguments(extensionConfiguration.getExtension());

    return extensionConfiguration.getExtension()
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
  private static void checkArguments(Map<String, FieldExtensionDefinition.Extension> extension) {
    for (var entry : extension.entrySet()) {
      if (!entry.getKey().equals(entry.getValue().getKey())) {
        throw new IllegalStateException("Extension map key not matching extension key: "
          + entry.getKey() + " vs " + entry.getValue().getKey());
      }
    }
  }
}

