package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.ProtocolElementDto;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProtocolElementRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private ProtocolElementRepository protocolElementRepository;

  @Test
  public void findAll_VocabularyConfiguration() {
    List<ProtocolElementDto> protocolElementDtos =
            protocolElementRepository.findAll("");
    assertTrue(protocolElementDtos.size() >= 2);
  }

}
