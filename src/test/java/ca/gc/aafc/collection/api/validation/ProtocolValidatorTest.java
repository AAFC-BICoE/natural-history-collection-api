package ca.gc.aafc.collection.api.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.entities.Protocol;
import ca.gc.aafc.collection.api.testsupport.factories.ProtocolFactory;

import java.util.List;
import javax.inject.Inject;

public class ProtocolValidatorTest extends CollectionModuleBaseIT {

  @Inject
  private ProtocolValidator validator;

  @Inject
  private MessageSource messageSource;

  @Test
  void validate_NoValidationException_ValidationSuccess() {
    Protocol protocol = ProtocolFactory.newProtocol()
      .protocolData(ProtocolFactory.newProtocolDataList())
      .build();

    Errors errors = new BeanPropertyBindingResult(protocol, protocol.getUuid().toString());
    validator.validate(protocol, errors);
    Assertions.assertEquals(0, errors.getAllErrors().size());
  }

  @Test
  void validate_invalidVocabularyBased_ErrorsReturned() {
    Protocol.ProtocolData protocolData = ProtocolFactory.newProtocolData()
      .vocabularyBased(true)
      .key("abc")
      .build();

    Protocol protocol = ProtocolFactory.newProtocol()
      .protocolData(List.of(protocolData))
      .build();

    Errors errors = new BeanPropertyBindingResult(protocol, protocol.getUuid().toString());
    validator.validate(protocol, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertTrue(errors.getAllErrors().get(0).getDefaultMessage().contains("Value not found in"));

    // Test a valid key but without setting vocabularyBased
    Protocol.ProtocolData protocolData2 = ProtocolFactory.newProtocolData()
      .vocabularyBased(false)
      .build();
    Protocol protocol2 = ProtocolFactory.newProtocol()
      .protocolData(List.of(protocolData2))
      .build();

    errors = new BeanPropertyBindingResult(protocol2, protocol2.getUuid().toString());
    validator.validate(protocol2, errors);
    Assertions.assertEquals(1, errors.getAllErrors().size());
    Assertions.assertTrue(errors.getAllErrors().get(0).getDefaultMessage().contains("cannot be used"));
  }

}
