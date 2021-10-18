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
    assertEquals(2, listOfExtensionDtos.size());

    List<Extension> listOfExtension = new ArrayList<>();
    for (ExtensionDto vocabularyDto : listOfExtensionDtos) {
      listOfExtension.add(vocabularyDto.getExtension());
    }

    MatcherAssert.assertThat(
      listOfExtension,
      Matchers.containsInAnyOrder(
        collectionExtensionConfiguration.getExtension().get(0),
        collectionExtensionConfiguration.getExtension().get(1)
      ));
  }
  
}
