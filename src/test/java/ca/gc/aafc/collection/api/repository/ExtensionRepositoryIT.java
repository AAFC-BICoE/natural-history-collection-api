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
    assertEquals(6, listOfExtensionDtos.size());

    List<Extension> listOfExtension = new ArrayList<>();
    for (ExtensionDto extensionDto : listOfExtensionDtos) {
      listOfExtension.add(extensionDto.getExtension());
    }

    MatcherAssert.assertThat(
      listOfExtension,
      Matchers.containsInAnyOrder(
        collectionExtensionConfiguration.getExtension().get("mixs_food-farmEnvironment"),
        collectionExtensionConfiguration.getExtension().get("mixs_soil"),
        collectionExtensionConfiguration.getExtension().get("mixs_sediment"),
        collectionExtensionConfiguration.getExtension().get("cfia_ppc"),
        collectionExtensionConfiguration.getExtension().get("phac_human_rg"),
        collectionExtensionConfiguration.getExtension().get("phac_animal_rg"),
        collectionExtensionConfiguration.getExtension().get("phac_cl")
      ));
  }
  
}
