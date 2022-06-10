package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.service.MaterialSampleService;
import ca.gc.aafc.collection.api.service.OrganismService;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.factories.OrganismEntityFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * The goal is to offer specific methods for testing where the Spring Transaction AOP will work.
 * It allows to control the transaction boundary for a test.
 * When called from outside this class, the method will commit at the end. Remember to cleanup if the commit succeed.
 */
@Service
public class TransactionalMethodProvider {

  @Inject
  protected MaterialSampleService materialSampleService;

  @Inject
  protected OrganismService organismService;

  /**
   * Method used by {@link OrganismAfterCommitCRUDIT#targetOrganism_MixedTargetOrganism_Exception()}.
   * Exception expected.
   */
  @Transactional
  public void runTransactionFor_TargetOrganism_MixedTargetOrganism_Exception() {
    List<Organism> organisms = new ArrayList<>();

    MaterialSample materialSample = MaterialSampleFactory.newMaterialSample().build();
    materialSampleService.createAndFlush(materialSample);

    Determination determination = Determination.builder()
            .isPrimary(false)
            .isFiledAs(false)
            .verbatimScientificName("verbatimScientificName")
            .build();

    Organism organism1 = OrganismEntityFactory.newOrganism()
            .isTarget(true)
            .determination(List.of(determination))
            .build();
    organisms.add(organismService.createAndFlush(organism1));

    Organism organism2 = OrganismEntityFactory.newOrganism()
            .isTarget(null)
            .determination(List.of(determination))
            .build();
    organisms.add(organismService.createAndFlush(organism2));

    // The material sample only gets set to the organism from the material sample service.
    materialSample.setOrganism(organisms);
    materialSampleService.update(materialSample);
  }
}
