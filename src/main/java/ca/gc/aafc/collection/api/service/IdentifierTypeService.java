package ca.gc.aafc.collection.api.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.config.TypedVocabularyConfiguration;
import ca.gc.aafc.collection.api.dto.IdentifierTypeDto;
import ca.gc.aafc.dina.service.CollectionBackedReadOnlyDinaService;

@Service
public class IdentifierTypeService extends CollectionBackedReadOnlyDinaService<String, IdentifierTypeDto> {

  public IdentifierTypeService(TypedVocabularyConfiguration typedVocabularyConfiguration) {
    super(typedVocabularyConfiguration.getIdentifierType()
      .stream()
      .map(item -> new IdentifierTypeDto(item.getKey(), item.getVocabularyElementType(),
        item.getMultilingualTitle()))
      .collect(Collectors.toList()), IdentifierTypeDto::getId);
  }
}
