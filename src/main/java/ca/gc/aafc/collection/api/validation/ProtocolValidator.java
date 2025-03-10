package ca.gc.aafc.collection.api.validation;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.config.CollectionVocabularyConfiguration;
import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.dina.validation.VocabularyBasedValidator;

import java.util.List;

import javax.inject.Named;

@Component
public class ProtocolValidator extends VocabularyBasedValidator<Protocol> {

  public static final String VOCABULARY_KEY_USED_WITH_NON_VOCABULARY_BASED = "validation.constraint.violation.protocol.vocabularyKeyUsedAsNonVocabularyBased";

  private static final String PROTOCOL_TYPE_FIELD_NAME = "protocolType";
  private static final String PROTOCOL_DATA_FIELD_NAME = "protocolData";

  private final List<CollectionVocabularyConfiguration.CollectionVocabularyElement> protocolTypeVocabulary;
  private final List<CollectionVocabularyConfiguration.CollectionVocabularyElement> protocolDataVocabulary;
  private final MessageSource messageSource;

  public ProtocolValidator(
      @Named("validationMessageSource") MessageSource baseValidationMessageSource,
      MessageSource messageSource,
      CollectionVocabularyConfiguration collectionVocabularyConfiguration) {

    super(Protocol.class, baseValidationMessageSource);
    this.messageSource = messageSource;

    this.protocolTypeVocabulary = collectionVocabularyConfiguration.getVocabularyByKey(
        CollectionVocabularyConfiguration.PROTOCOL_TYPE_VOCAB_KEY);
    this.protocolDataVocabulary = collectionVocabularyConfiguration.getVocabularyByKey(
        CollectionVocabularyConfiguration.PROTOCOL_DATA_VOCAB_KEY);
  }

  @Override
  public void validateTarget(Protocol target, Errors errors) {

    validateProtocolType(target, errors);

    List<Protocol.ProtocolData> protocolData = target.getProtocolData();
    if (protocolData == null) {
      return;
    }

    for (Protocol.ProtocolData p : protocolData) {
      if (p.isVocabularyBased()) {
        p.setKey(validateAndStandardizeValueAgainstVocabulary(p.getKey(), PROTOCOL_DATA_FIELD_NAME,
            protocolDataVocabulary, errors));
      } else {
        // make sure a valid vocabulary key is not used with a protocol data if
        // isVocabularyBased is not true
        if (findInVocabulary(p.getKey(), protocolDataVocabulary).isPresent()) {
          errors.rejectValue(PROTOCOL_DATA_FIELD_NAME,
              VOCABULARY_KEY_USED_WITH_NON_VOCABULARY_BASED,
              this.messageSource.getMessage(VOCABULARY_KEY_USED_WITH_NON_VOCABULARY_BASED, null,
                  LocaleContextHolder.getLocale()));
        }
      }
    }
  }

  private void validateProtocolType(Protocol target, Errors errors) {
    if (StringUtils.isBlank(target.getProtocolType())) {
      return;
    }

    target.setProtocolType(validateAndStandardizeValueAgainstVocabulary(target.getProtocolType(),
        PROTOCOL_TYPE_FIELD_NAME,
        protocolTypeVocabulary, errors));
  }
}
