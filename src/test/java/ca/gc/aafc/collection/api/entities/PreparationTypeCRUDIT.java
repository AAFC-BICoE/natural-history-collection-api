package ca.gc.aafc.collection.api.entities;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationTypeFactory;
import ca.gc.aafc.dina.i18n.MultilingualDescription;

public class PreparationTypeCRUDIT extends CollectionModuleBaseIT {

  private static final String EXPECTED_NAME = "name";
  private static final String EXPECTED_GROUP = "DINA GROUP";
  private static final String EXPECTED_CREATED_BY = "createdBy";

  private static final MultilingualDescription.MultilingualPair MULTILINGUAL_PAIR_FR =
      MultilingualDescription.MultilingualPair.of("fr", "description en français");

  private static final MultilingualDescription.MultilingualPair MULTILINGUAL_PAIR_EN =
      MultilingualDescription.MultilingualPair.of("en", "description in english");

  private static final MultilingualDescription MULTILINGUAL_DESCRIPTION = MultilingualDescription.builder()
    .descriptions(List.of(MULTILINGUAL_PAIR_EN, MULTILINGUAL_PAIR_FR))
    .build();

  private PreparationType preparationType;

  @BeforeEach
  void setup() {

    preparationType = PreparationTypeFactory.newPreparationType()
      .name(EXPECTED_NAME)
      .group(EXPECTED_GROUP)
      .createdBy(EXPECTED_CREATED_BY)
      .multilingualDescription(MULTILINGUAL_DESCRIPTION)
      .build();
    preparationTypeService.create(preparationType);
  }

  @Test
  void create() {
    Assertions.assertNotNull(preparationType.getId());
    Assertions.assertNotNull(preparationType.getCreatedOn());
    Assertions.assertNotNull(preparationType.getUuid());
  }

  @Test
  void find() {
    PreparationType result = preparationTypeService.findOne(
      preparationType.getUuid(),
      PreparationType.class);
    Assertions.assertEquals(EXPECTED_NAME, result.getName());
    Assertions.assertEquals(EXPECTED_GROUP, result.getGroup());
    Assertions.assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
    Assertions.assertEquals(MULTILINGUAL_DESCRIPTION.getDescriptions(), result.getMultilingualDescription().getDescriptions());
  }
}
