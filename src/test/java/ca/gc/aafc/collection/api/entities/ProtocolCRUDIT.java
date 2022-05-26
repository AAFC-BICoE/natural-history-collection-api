package ca.gc.aafc.collection.api.entities;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;
import ca.gc.aafc.collection.api.testsupport.factories.PreparationTypeFactory;
import ca.gc.aafc.dina.i18n.MultilingualDescription;

import javax.persistence.PersistenceException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ProtocolCRUDIT extends CollectionModuleBaseIT {

  private static final String EXPECTED_NAME = "name";
  private static final String EXPECTED_GROUP = "DINA GROUP";
  private static final String EXPECTED_CREATED_BY = "createdBy";
  private final List<UUID> attachmentIdentifiers = List.of(UUID.randomUUID(), UUID.randomUUID());

  private static final MultilingualDescription.MultilingualPair MULTILINGUAL_PAIR_FR =
      MultilingualDescription.MultilingualPair.of("fr", "description en français");

  private static final MultilingualDescription.MultilingualPair MULTILINGUAL_PAIR_EN =
      MultilingualDescription.MultilingualPair.of("en", "description in english");

  private static final MultilingualDescription MULTILINGUAL_DESCRIPTION = MultilingualDescription.builder()
    .descriptions(List.of(MULTILINGUAL_PAIR_EN, MULTILINGUAL_PAIR_FR))
    .build();


  private PreparationType buildTestPreparationType() {
    return  PreparationTypeFactory.newPreparationType()
      .name(EXPECTED_NAME)
      .attachments(attachmentIdentifiers)
      .group(EXPECTED_GROUP)
      .createdBy(EXPECTED_CREATED_BY)
      .multilingualDescription(MULTILINGUAL_DESCRIPTION)
      .build();
  }

  @Test
  void create() {
    Protocol protocol = buildTestPreparationType();
    protocolService.create(preparationType);

    assertNotNull(protocol.getId());
    assertNotNull(protocol.getCreatedOn());
    assertNotNull(protocol.getUuid());
  }

  @Test
  void createDuplicates_ThrowsException() {
    Protocol protocol = buildTestProtocol();
    protocolService.createAndFlush(protocol);

    Protocol protocol = buildTestProtocol();
    //change the group so create should work
    protocol2.setGroup("grp2");
    protocolService.createAndFlush(protocol2);

    //set the same group and try to update
    protocol2.setGroup(EXPECTED_GROUP);
    assertThrows(PersistenceException.class, () -> protocolService.createAndFlush(protocol2));
  }

  @Test
  void find() {
    Protocol protocol = buildTestProtocol();
    protocolService.create(protocol);

    Protocol result = protocolService.findOne(
    protocol.getUuid(),
    Protocol.class);
    Assertions.assertEquals(EXPECTED_NAME, result.getName());
    Assertions.assertEquals(EXPECTED_GROUP, result.getGroup());
    Assertions.assertEquals(EXPECTED_ATTACHMENTS, result.getAttchments());
    Assertions.assertEquals(EXPECTED_CREATED_BY, result.getCreatedBy());
    Assertions.assertEquals(MULTILINGUAL_DESCRIPTION.getDescriptions(), result.getMultilingualDescription().getDescriptions());
  }
}
