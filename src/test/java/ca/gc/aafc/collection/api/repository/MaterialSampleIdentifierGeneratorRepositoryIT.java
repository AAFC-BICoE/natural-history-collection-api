package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleIdentifierGeneratorDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.entities.MaterialSampleNameGeneration;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import java.util.List;

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
      .quantity(2)
      .build();

    MaterialSampleIdentifierGeneratorDto dto = materialSampleIdentifierGeneratorRepository.create(generatedDto);
    List<String> nextIdentifiers = dto.getNextIdentifiers().get(child1.getUuid());
    assertEquals("ABC-01-b", nextIdentifiers.get(0));
    assertEquals("ABC-01-c", nextIdentifiers.get(1));
  }

  @Test
  @WithMockKeycloakUser(groupRole = {"aafc:user"})
  public void materialSampleIdentifierGeneratorRepositoryMultipleNext_nextIdentifierReturned() {

    MaterialSampleDto parentDto = MaterialSampleTestFixture.newMaterialSample();
    parentDto.setMaterialSampleType(MaterialSample.MaterialSampleType.WHOLE_ORGANISM);
    parentDto.setMaterialSampleName("ABC-01");
    parentDto = materialSampleRepository.create(parentDto);

    MaterialSampleDto child1 = MaterialSampleTestFixture.newMaterialSample();
    child1.setParentMaterialSample(parentDto);
    child1.setMaterialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN);
    child1.setMaterialSampleName("ABC-01-a");
    child1 = materialSampleRepository.create(child1);

    MaterialSampleDto child2 = MaterialSampleTestFixture.newMaterialSample();
    child2.setParentMaterialSample(parentDto);
    child2.setMaterialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN);
    child2.setMaterialSampleName("ABC-01-b");
    child2 = materialSampleRepository.create(child2);

    MaterialSampleIdentifierGeneratorDto generatedDto = MaterialSampleIdentifierGeneratorDto.builder()
      .currentParentsUUID(List.of(child1.getUuid(), child2.getUuid()))
      .strategy(MaterialSampleNameGeneration.IdentifierGenerationStrategy.TYPE_BASED)
      .characterType(MaterialSampleNameGeneration.CharacterType.LOWER_LETTER)
      .materialSampleType(MaterialSample.MaterialSampleType.CULTURE_STRAIN)
      .build();

    MaterialSampleIdentifierGeneratorDto dto = materialSampleIdentifierGeneratorRepository.create(generatedDto);
    List<String> nextIdentifiers = dto.getNextIdentifiers().get(child1.getUuid());
    assertEquals("ABC-01-c", nextIdentifiers.get(0));

    nextIdentifiers = dto.getNextIdentifiers().get(child2.getUuid());
    assertEquals("ABC-01-d", nextIdentifiers.get(0));
  }

}
