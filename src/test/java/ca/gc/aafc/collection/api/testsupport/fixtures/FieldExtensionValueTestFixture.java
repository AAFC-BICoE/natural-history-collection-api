package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Field;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.collection.api.dto.FieldExtensionValueDto;
import java.util.List;

public class FieldExtensionValueTestFixture {

  public static final String GROUP = "dina";

  public static final String FIELD_KEY = "tot_org_carb";

  public static final String EXT_NAME = "MIxS Soil v4";
  public static final String EXT_KEY = "mixs_soil_v4";

  public static final String FIELD_EXTENSION_KEY = EXT_KEY + "." + FIELD_KEY;

  public static FieldExtensionValueDto newFieldExtensionValueDto() {
    Field field = Field.builder().key(FIELD_KEY)
      .name("total organic carbon").dinaComponent("COLLECTING_EVENT")
      .multilingualDescription(new MultilingualDescription(List.of(MultilingualDescription
        .MultilingualPair.of("en", "total organic carbon content"))))
      .build();

    return new FieldExtensionValueDto(FIELD_EXTENSION_KEY, EXT_NAME, EXT_KEY, field);
  }

}
