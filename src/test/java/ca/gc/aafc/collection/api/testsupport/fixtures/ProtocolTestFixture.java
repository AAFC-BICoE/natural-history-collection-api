package ca.gc.aafc.collection.api.testsupport.fixtures;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.dto.ProtocolDto;
import ca.gc.aafc.collection.api.testsupport.factories.ProtocolFactory;

public class ProtocolTestFixture {

  public static final String GROUP = "aafc";

  public static ProtocolDto newProtocol() {
    ProtocolDto protocolDto = new ProtocolDto();
    protocolDto.setName(RandomStringUtils.randomAlphabetic(5));
    protocolDto.setMultilingualDescription(MultilingualTestFixture.newMultilingualDescription());
    protocolDto.setGroup(GROUP);
    protocolDto.setProtocolData(ProtocolFactory.newProtocolDataList());

    return protocolDto;
  }

}
