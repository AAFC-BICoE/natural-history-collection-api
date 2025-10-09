package ca.gc.aafc.collection.api.mapper;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.collection.api.entities.CollectingEvent;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.testsupport.factories.AssemblageFactory;
import ca.gc.aafc.collection.api.testsupport.factories.CollectingEventFactory;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.AssemblageTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.dina.datetime.ISODateTime;
import ca.gc.aafc.dina.dto.ExternalRelationDto;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MaterialSampleMapperTest {

  private static final MaterialSampleMapper MAPPER = MaterialSampleMapper.INSTANCE;

  @Test
  public void testToEntity() {
    MaterialSampleDto dto = MaterialSampleTestFixture.newMaterialSample();
    // Set specific values instead of relying on the fixture
    dto.setDwcCatalogNumber("CAT-123");
    dto.setMaterialSampleName("Sample A");
    dto.setMaterialSampleState("Preserved");
    dto.setMaterialSampleRemarks("Test remarks");
    dto.setPreparationDate(LocalDate.of(2023, 1, 15));
    dto.setDwcOtherCatalogNumbers(new String[]{"OTHER-1", "OTHER-2"});
    dto.setCreatedBy("user1");
    dto.setGroup("group1");

    // external relationships
    dto.setAttachment(List.of(ExternalRelationDto.builder().id(UUID.randomUUID().toString()).type("metadata").build()));

    Map<String, Object> asMap = JsonAPITestHelper.toAttributeMap(dto);
    Set<String> attributesName = new HashSet<>(asMap.keySet());
    //explicitly add it since it's not an attribute
    attributesName.add("attachment");

    MaterialSample entity = MAPPER.toEntity(dto, attributesName, null);

    assertEquals(dto.getDwcCatalogNumber(), entity.getDwcCatalogNumber());
    assertEquals(dto.getMaterialSampleName(), entity.getMaterialSampleName());
    assertEquals(dto.getMaterialSampleState(), entity.getMaterialSampleState());
    assertEquals(dto.getMaterialSampleRemarks(), entity.getMaterialSampleRemarks());
    assertEquals(dto.getPreparationDate(), entity.getPreparationDate());
    assertArrayEquals(dto.getDwcOtherCatalogNumbers(), entity.getDwcOtherCatalogNumbers());
    assertEquals(dto.getCreatedBy(), entity.getCreatedBy());
    assertEquals(dto.getGroup(), entity.getGroup());

    // check nested structures
    assertNotNull(dto.getRestrictionFieldsExtension().get(MaterialSampleTestFixture.RESTRICTION_KEY));
    assertEquals(dto.getRestrictionFieldsExtension(), entity.getRestrictionFieldsExtension());

    // should be null since relationships are not mapped by the mapper
    assertNull(entity.getAttachment());
  }

  @Test
  public void testToDto() {

    MaterialSample entity = MaterialSampleFactory.newMaterialSampleNoRelationships().build();
    entity.setDwcCatalogNumber("CAT-1234");
    entity.setMaterialSampleName("Sample B");
    entity.setMaterialSampleState("Preserved");
    entity.setMaterialSampleRemarks("Test remarks 3");
    entity.setPreparationDate(LocalDate.of(2023, 1, 16));
    entity.setDwcOtherCatalogNumbers(new String[]{"OTHER-3", "OTHER-4"});
    entity.setCreatedBy("user2");
    entity.setGroup("group2");

    // external relationships
    entity.setAttachment(List.of(UUID.randomUUID()));

    // we are using all attributes as provided
    Set<String> attributesName =
      new HashSet<>(JsonAPITestHelper.toAttributeMap(MaterialSampleTestFixture.newMaterialSample())
        .keySet());
    //explicitly add it since it's not an attribute
    attributesName.add("attachment");

    MaterialSampleDto dto = MAPPER.toDto(entity, attributesName, null);

    assertEquals(entity.getDwcCatalogNumber(), dto.getDwcCatalogNumber());
    assertEquals(entity.getMaterialSampleName(), dto.getMaterialSampleName());
    assertEquals(entity.getMaterialSampleState(), dto.getMaterialSampleState());
    assertEquals(entity.getMaterialSampleRemarks(), dto.getMaterialSampleRemarks());
    assertEquals(entity.getPreparationDate(), dto.getPreparationDate());
    assertArrayEquals(entity.getDwcOtherCatalogNumbers(), dto.getDwcOtherCatalogNumbers());
    assertEquals(entity.getCreatedBy(), dto.getCreatedBy());
    assertEquals(entity.getGroup(), dto.getGroup());

    // make sure external relationship is mapped in entity to dto
    assertEquals(entity.getAttachment().getFirst().toString(), dto.getAttachment().getFirst().getId());
  }

  @Test
  public void testToDtoRelationships() {

    MaterialSample entity = MaterialSampleFactory.newMaterialSampleNoRelationships().build();

    CollectingEvent collectingEventTest = CollectingEventFactory.newCollectingEvent()
      .build();

    Assemblage assemblageTest = AssemblageFactory.newAssemblage().build();

    entity.setAssemblages(List.of(assemblageTest));
    entity.setCollectingEvent(collectingEventTest);

    // we are using all attributes as provided
    Set<String> attributesName =
      new HashSet<>(JsonAPITestHelper.toAttributeMap(MaterialSampleTestFixture.newMaterialSample())
        .keySet());

    addRelationshipAttributes(attributesName, MaterialSample.ASSEMBLAGES_PROP_NAME, JsonAPITestHelper.toAttributeMap(assemblageTest)
      .keySet());
    addRelationshipAttributes(attributesName, MaterialSample.COLLECTING_EVENT_PROP_NAME, JsonAPITestHelper.toAttributeMap(collectingEventTest)
      .keySet());

    // we need to explicitly add it since it won't be part of the attribute since the value is null
    // but, we still want to test the conversion with null value
    attributesName.add(MaterialSample.COLLECTING_EVENT_PROP_NAME + ".startEventDateTime");

    MaterialSampleDto dto = MAPPER.toDto(entity, attributesName, null);
    assertEquals(assemblageTest.getName(), dto.getAssemblages().getFirst().getName());
  }

  private static void addRelationshipAttributes(Set<String> attributesName, String relName, Set<String> relAttributesName) {
    attributesName.add(relName);
    attributesName.addAll(relAttributesName.stream().map( e -> relName + "." + e).collect(Collectors.toSet()));
  }

  @Test
  public void testPatchEntity() {

    MaterialSample entity = MaterialSampleFactory.newMaterialSampleNoRelationships().build();

    MaterialSampleDto patchDto = new MaterialSampleDto();
    patchDto.setMaterialSampleName(TestableEntityFactory.generateRandomNameLettersOnly(8));

    MAPPER.patchEntity(entity, patchDto, Set.of("materialSampleName"), null);
    assertEquals(entity.getMaterialSampleName(), patchDto.getMaterialSampleName());
  }
}
