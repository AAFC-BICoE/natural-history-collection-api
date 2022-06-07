package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.MaterialSampleIdentifierGeneratorDto;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaterialSampleIdentifierGeneratorRepositoryIT extends BaseRepositoryIT {

  @Inject
  private MaterialSampleIdentifierGeneratorRepository materialSampleIdentifierGeneratorRepository;

  @Test
  public void materialSampleIdentifierGeneratorRepositoryNext_nextIdentifierReturned() {
    MaterialSampleIdentifierGeneratorDto dto = materialSampleIdentifierGeneratorRepository.findOne("ABC-vw", new QuerySpec(MaterialSampleIdentifierGeneratorDto.class));
    assertEquals("ABC-vx", dto.getNextIdentifier());
  }

}
