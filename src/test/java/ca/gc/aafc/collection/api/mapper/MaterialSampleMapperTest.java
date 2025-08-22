package ca.gc.aafc.collection.api.mapper;

import java.util.Set;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.MaterialSampleDto;
import ca.gc.aafc.collection.api.entities.MaterialSample;
import ca.gc.aafc.collection.api.testsupport.fixtures.MaterialSampleTestFixture;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaterialSampleMapperTest {

  @Test
  public void testToEntity() {
    MaterialSampleMapper mapper = MaterialSampleMapper.INSTANCE;

    MaterialSampleDto dto = MaterialSampleTestFixture.newMaterialSample();

    MaterialSample entity = mapper.toEntity(dto, Set.of("dwcCatalogNumber"), null);

    assertEquals(dto.getDwcCatalogNumber(), entity.getDwcCatalogNumber());
  }
}
