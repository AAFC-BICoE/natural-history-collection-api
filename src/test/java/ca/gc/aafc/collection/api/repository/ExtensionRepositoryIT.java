package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionExtensionConfiguration;
import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.dto.ExtensionDto;
import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Extension;
import io.crnk.core.queryspec.QuerySpec;

public class ExtensionRepositoryIT extends CollectionModuleBaseIT {

  @Inject
  private ExtensionRepository extensionRepository;

  @Inject
  private CollectionExtensionConfiguration collectionExtensionConfiguration;

  @Test
  public void findAll_ExtensionConfiguration() {
    List<ExtensionDto> listOfExtensionDtos =
      extensionRepository.findAll(new QuerySpec(ExtensionDto.class));
    assertEquals(11, listOfExtensionDtos.size());

    List<Extension> listOfExtension = new ArrayList<>();
    for (ExtensionDto extensionDto : listOfExtensionDtos) {
      listOfExtension.add(extensionDto.getExtension());
    }

    MatcherAssert.assertThat(
      listOfExtension,
      Matchers.containsInAnyOrder(
        collectionExtensionConfiguration.getExtension().get("mixs_soil_v5"),
        collectionExtensionConfiguration.getExtension().get("mixs_sediment_v5"),
        collectionExtensionConfiguration.getExtension().get("mixs_soil_v4"),
        collectionExtensionConfiguration.getExtension().get("mixs_sediment_v4"),
        collectionExtensionConfiguration.getExtension().get("mixs_plant_associated_v4"),
        collectionExtensionConfiguration.getExtension().get("mixs_water_v4"),
        collectionExtensionConfiguration.getExtension().get("cfia_ppc"),
        collectionExtensionConfiguration.getExtension().get("phac_human_rg"),
        collectionExtensionConfiguration.getExtension().get("phac_animal_rg"),
        collectionExtensionConfiguration.getExtension().get("phac_cl"),
        collectionExtensionConfiguration.getExtension().get("agronomy_ontology_v1")
      ));
  }
  
}
