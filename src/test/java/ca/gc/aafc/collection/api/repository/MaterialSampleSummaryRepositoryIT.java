package ca.gc.aafc.collection.api.repository;

import io.crnk.core.queryspec.QuerySpec;
import java.net.MalformedURLException;
import java.util.List;
import javax.inject.Inject;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleSummaryDto;
import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.DeterminationFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.OrganismTestFixture;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MaterialSampleSummaryRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private MaterialSampleRepository materialSampleRepository;

  @Inject
  private OrganismRepository organismRepository;

  @Inject
  private MaterialSampleSummaryRepository materialSampleSummaryRepository;

  @Test
  public void onMaterialSampleWithDetermination_repoFindOneReturnRightSummary() throws MalformedURLException {
    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
    OrganismDto organismDto = OrganismTestFixture.newOrganism(DeterminationFixture.newDetermination());
    organismDto.setIsTarget(true);

    organismDto = organismRepository.create(organismDto);
    materialSampleDto.setOrganism(List.of(organismDto));
    materialSampleDto = materialSampleRepository.create(materialSampleDto);

    // we need to flush to make sure it will be visible in the PG view
    organismService.flush();

    MaterialSampleSummaryDto mssDto = materialSampleSummaryRepository.findOne(materialSampleDto.getUuid(), new QuerySpec(
      MaterialSampleSummaryDto.class));

    assertNotNull(mssDto.getEffectiveDeterminations());
    assertEquals(1, mssDto.getEffectiveDeterminations().size());
  }

}
