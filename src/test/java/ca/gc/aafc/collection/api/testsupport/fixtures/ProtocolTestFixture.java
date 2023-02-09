package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.collection.api.entities.Protocol;
import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.ProtocolDto;

import java.util.List;

public class ProtocolTestFixture {

  public static final String GROUP = "aafc";

  public static ProtocolDto newProtocol() {
    ProtocolDto protocolDto = new ProtocolDto();
    protocolDto.setName(RandomStringUtils.randomAlphabetic(5));
    protocolDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    protocolDto.setGroup(GROUP);

    protocolDto.setProtocolData(newProtocolData());

    return protocolDto;
  }

  public static List<Protocol.ProtocolData> newProtocolData() {

    List<Protocol.ProtocolDataElement> protocolDataElements =
            List.of(Protocol.ProtocolDataElement.builder()
                    .elementType("concentration")
                    .vocabularyBased(true)
                    .value("0.3")
                    .unit("uL.rxn").build());

    return List.of(Protocol.ProtocolData.builder()
            .key("forward_primer")
            .vocabularyBased(true)
            .protocolDataElement(protocolDataElements)
            .build());
  }
  
}
