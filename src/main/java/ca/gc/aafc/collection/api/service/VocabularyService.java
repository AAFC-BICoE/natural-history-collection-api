package ca.gc.aafc.collection.api.service;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.dto.VocabularyDto;
import ca.gc.aafc.dina.service.CollectionBackedReadOnlyDinaService;

import java.util.stream.Collectors;

@Service
public class VocabularyService extends CollectionBackedReadOnlyDinaService<String, VocabularyDto> {

  public VocabularyService(CollectionVocabularyConfiguration collectionVocabularyConfiguration) {
    super(collectionVocabularyConfiguration.getVocabulary()
      .entrySet()
      .stream()
      .map(entry -> new VocabularyDto(entry.getKey(), entry.getValue()))
      .collect(Collectors.toList()), VocabularyDto::getId);
  }

}
