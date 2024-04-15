package ca.gc.aafc.collection.api.service;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.dto.VocabularyDto;
import ca.gc.aafc.dina.service.PredicateBasedReadOnlyDinaService;

@Service
public class VocabularyService implements PredicateBasedReadOnlyDinaService<String, VocabularyDto> {

  private final List<VocabularyDto> vocabulary;

  public VocabularyService(CollectionVocabularyConfiguration collectionVocabularyConfiguration) {
    vocabulary = collectionVocabularyConfiguration.getVocabulary()
      .entrySet()
      .stream()
      .map( entry -> new VocabularyDto(entry.getKey(), entry.getValue()))
      .collect( Collectors.toList());
  }

  @Override
  public VocabularyDto findOne(String key) {
    return vocabulary.stream().filter( d -> key.equals(d.getId())).findFirst().orElse(null);
  }

  @Override
  public List<VocabularyDto> findAll(Predicate<VocabularyDto> predicate, Integer pageOffset,
                                     Integer pageLimit) {

    Stream<VocabularyDto> stream = vocabulary.stream().filter(predicate);

    if(pageOffset != null) {
      stream = stream.skip(pageOffset);
    }
    if(pageLimit != null) {
      stream = stream.limit(pageLimit);
    }
    return stream.collect(Collectors.toList());
  }
}
