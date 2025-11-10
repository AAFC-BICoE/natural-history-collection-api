package ca.gc.aafc.collection.api.repository;

import io.crnk.core.queryspec.QuerySpec;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.dto.MaterialSampleSummaryDto;
import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.DeterminationFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.OrganismTestFixture;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

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

    OrganismDto organismDto = OrganismTestFixture.newOrganism(DeterminationFixture.newDetermination());
    organismDto.setIsTarget(true);
    JsonApiDocument organismToCreate = JsonApiDocuments.createJsonApiDocument(
      null, OrganismDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(organismDto)
    );
    UUID organismUUID =
      JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(organismRepository
        .onCreate(organismToCreate));

    MaterialSampleDto materialSampleDto = MaterialSampleTestFixture.newMaterialSample();
    JsonApiDocument materialSampleToCreate = JsonApiDocuments.createJsonApiDocument(
      null, MaterialSampleDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(materialSampleDto),
      Map.of("organism", List.of(JsonApiDocument.ResourceIdentifier.builder().id(organismUUID).type(OrganismDto.TYPENAME)))
    );

    UUID matSampleId =
      JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(materialSampleRepository
        .onCreate(materialSampleToCreate));

    // we need to flush to make sure it will be visible in the PG view
    organismService.flush();

    MaterialSampleSummaryDto mssDto = materialSampleSummaryRepository.findOne(matSampleId, new QuerySpec(
      MaterialSampleSummaryDto.class));

    assertNotNull(mssDto.getEffectiveDeterminations());
    Assertions.assertEquals(1, mssDto.getEffectiveDeterminations().size());
  }

}
