package ca.gc.aafc.collection.api.validation;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.Protocol;

import java.util.List;

@Component
public class ProtocolValidator extends VocabularyBasedValidator<Protocol> {

  private final List<CollectionVocabularyConfiguration.CollectionVocabularyElement> protocolDataVocabulary;

  public ProtocolValidator(MessageSource messageSource, CollectionVocabularyConfiguration collectionVocabularyConfiguration) {
    super(messageSource, Protocol.class);
    this.protocolDataVocabulary = collectionVocabularyConfiguration.getVocabularyByKey(CollectionVocabularyConfiguration.PROTOCOL_DATA_VOCAB_KEY);
  }

  @Override
  protected void validateVocabularyBasedAttribute(Protocol target, Errors errors) {
    List<Protocol.ProtocolData> protocolData = target.getProtocolData();
    if(protocolData == null) {
      return;
    }

    for(Protocol.ProtocolData p : protocolData) {
      if( p.isVocabularyBased()) {
        p.setKey(validateAndStandardizeValueAgainstVocabulary(p.getKey(), "protocolData", protocolDataVocabulary, errors));
      }
    }
  }
}
