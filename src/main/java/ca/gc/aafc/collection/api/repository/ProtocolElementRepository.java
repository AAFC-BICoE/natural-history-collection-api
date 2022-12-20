package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.config.ProtocolElementConfiguration;
import ca.gc.aafc.collection.api.dto.VocabularyDto;

import java.util.stream.Collectors;

public class ProtocolElementRepository {

  public ProtocolElementRepository(ProtocolElementConfiguration protocolElementConfiguration) {
    vocabulary = protocolElementConfiguration.getProtocolElements()
            .stream()
            .map( entry -> new VocabularyDto(entry.name(), entry.))
            .collect( Collectors.toList());
  }
}
