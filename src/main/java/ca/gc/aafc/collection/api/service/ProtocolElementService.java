package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.config.TypedVocabularyConfiguration;
import ca.gc.aafc.collection.api.dto.ProtocolElementDto;
import ca.gc.aafc.dina.service.CollectionBackedReadOnlyDinaService;

import java.util.stream.Collectors;

@Service
public class ProtocolElementService extends CollectionBackedReadOnlyDinaService<String, ProtocolElementDto> {

  public ProtocolElementService(TypedVocabularyConfiguration typedVocabularyConfiguration) {
    super(typedVocabularyConfiguration.getProtocolDataElement()
      .stream()
      .map(entry -> new ProtocolElementDto(entry.getKey(), entry.getTerm(),
        entry.getVocabularyElementType(), entry.getMultilingualTitle()))
      .collect(Collectors.toList()), ProtocolElementDto::getId);
  }

}
