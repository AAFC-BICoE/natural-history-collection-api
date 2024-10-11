package ca.gc.aafc.collection.api.entities;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.AssemblageFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MultilingualDescriptionFactory;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import org.junit.jupiter.api.Test;


import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AssemblageCRUDIT extends CollectionModuleBaseIT {

  private static final String EXPECTED_NAME = "name";
  private static final String EXPECTED_GROUP = "dina-group";
  private static final String EXPECTED_CREATED_BY = "createdBy";
  private final List<UUID> EXPECT_ATTACHMENTS = List.of(UUID.randomUUID(), UUID.randomUUID());

  private static final MultilingualDescription MULTILINGUAL_DESCRIPTION =
          MultilingualDescriptionFactory.newMultilingualDescription();

  @Test
  void create() {
    Assemblage assemblage = buildExpectedAssemblage();

    assemblageService.create(assemblage);

    assertNotNull(assemblage.getId());
    assertNotNull(assemblage.getCreatedOn());
    assertNotNull(assemblage.getUuid());
  }

  @Test
  void find() {
    Assemblage assemblage = buildExpectedAssemblage();
    assemblageService.create(assemblage);

    Assemblage result = assemblageService.findOne(
            assemblage.getUuid(),
            Assemblage.class);

    assertEquals(EXPECTED_NAME, result.getName());
    assertEquals(EXPECTED_GROUP, result.getGroup());
    assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
    assertEquals(EXPECT_ATTACHMENTS, result.getAttachment());
    assertEquals(MULTILINGUAL_DESCRIPTION.getDescriptions(), result.getMultilingualDescription().getDescriptions());
  }

  private Assemblage buildExpectedAssemblage() {
    return AssemblageFactory.newAssemblage()
            .name(EXPECTED_NAME)
            .group(EXPECTED_GROUP)
            .createdBy(EXPECTED_CREATED_BY)
            .multilingualDescription(MULTILINGUAL_DESCRIPTION)
            .attachment(EXPECT_ATTACHMENTS)
            .build();
  }
}
