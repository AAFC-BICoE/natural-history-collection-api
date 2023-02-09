package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.ProtocolElementDto;
import io.crnk.core.queryspec.QuerySpec;
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
            protocolElementRepository.findAll(new QuerySpec(ProtocolElementDto.class));
    assertTrue(protocolElementDtos.size() >= 2);
  }

}
