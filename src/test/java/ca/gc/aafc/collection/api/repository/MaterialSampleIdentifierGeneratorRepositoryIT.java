package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleIdentifierGeneratorDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.inject.Inject;

public class MaterialSampleIdentifierGeneratorRepositoryIT extends BaseRepositoryIT {

  @Inject
  private MaterialSampleIdentifierGeneratorRepository materialSampleIdentifierGeneratorRepository;

  @Inject
  protected MaterialSampleRepository materialSampleRepository;

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void materialSampleIdentifierGeneratorRepositoryNext_nextIdentifierReturned() {

    MaterialSampleDto parentDto = MaterialSampleTestFixture.newMaterialSample();
    parentDto.setMaterialSampleType(MaterialSample.MaterialSampleType.WHOLE_ORGANISM);
    parentDto.setMaterialSampleName("ABC-01");
    parentDto = materialSampleRepository.create(parentDto);

    MaterialSampleDto child1 = MaterialSampleTestFixture.newMaterialSample();
    child1.setParentMaterialSample(parentDto);
    child1.setMaterialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN);
    child1.setMaterialSampleName("ABC-01-a");
    child1 = materialSampleRepository.create(child1);

    MaterialSampleIdentifierGeneratorDto generatedDto = MaterialSampleIdentifierGeneratorDto.builder()
      .currentParentUUID(child1.getUuid())
      .strategy(MaterialSampleNameGeneration.IdentifierGenerationStrategy.TYPE_BASED)
      .characterType(MaterialSampleNameGeneration.CharacterType.LOWER_LETTER)
      .materialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN)
      .amount(2).build();

    MaterialSampleIdentifierGeneratorDto dto = materialSampleIdentifierGeneratorRepository.create(generatedDto);
    assertEquals("ABC-01-b", dto.getNextIdentifiers().get(0));
    assertEquals("ABC-01-c", dto.getNextIdentifiers().get(1));
  }

}
