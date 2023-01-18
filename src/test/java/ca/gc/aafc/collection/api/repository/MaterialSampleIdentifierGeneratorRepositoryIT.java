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
    MaterialSampleIdentifierGeneratorDto generatedDto = MaterialSampleIdentifierGeneratorDto.builder()
            .submittedIdentifier("ABC-vw")
            .amount(2).build();

    MaterialSampleIdentifierGeneratorDto dto = materialSampleIdentifierGeneratorRepository.create(generatedDto);
    assertEquals("ABC-vx", dto.getNextIdentifiers().get(0));
    assertEquals("ABC-vy", dto.getNextIdentifiers().get(1));
  }

}
