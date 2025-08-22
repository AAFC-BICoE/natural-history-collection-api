package ca.gc.aafc.collection.api.mapper;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.testsupport.factories.MaterialSampleFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaterialSampleMapperTest {

  private static final MaterialSampleMapper MAPPER = MaterialSampleMapper.INSTANCE;

  @Test
  public void testToEntity() {
    MaterialSampleDto dto = MaterialSampleTestFixture.newMaterialSample();
    Map<String, Object> asMap = JsonAPITestHelper.toAttributeMap(dto);

    MaterialSample entity = MAPPER.toEntity(dto, asMap.keySet(), null);

    assertEquals(dto.getDwcCatalogNumber(), entity.getDwcCatalogNumber());
    assertEquals(dto.getRestrictionFieldsExtension(), entity.getRestrictionFieldsExtension());
  }

  @Test
  public void testToDto() {

    MaterialSample entity = MaterialSampleFactory.newMaterialSampleNoRelationships().build();
    // we are using all attributes as provided
    Set<String> provided = JsonAPITestHelper.toAttributeMap(MaterialSampleTestFixture.newMaterialSample())
      .keySet();

    MaterialSampleDto dto = MAPPER.toDto(entity, provided, null);

    assertEquals(entity.getDwcCatalogNumber(), dto.getDwcCatalogNumber());
    assertEquals(entity.getRestrictionFieldsExtension(), dto.getRestrictionFieldsExtension());
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
