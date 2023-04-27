package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Field;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.collection.api.dto.FieldExtensionValueDto;
import java.util.List;

public class FieldExtensionValueTestFixture {

  public static final String GROUP = "dina";

  public static FieldExtensionValueDto newFieldExtensionValueDto() {
    String id = "mixs_microbial_v4.alkyl_diethers";
    String extensionName = "MIxS Microbial v4";
    String extensionKey = "mixs_microbial_v4";
    Field field = Field.builder().key("alkyl_diethers")
      .name("alkyl diethers").dinaComponent("COLLECTING_EVENT")
      .multilingualDescription(new MultilingualDescription(List.of(MultilingualDescription
        .MultilingualPair.of("en", "concentration of alkyl diethers "))))
      .build();

    return new FieldExtensionValueDto(id, extensionName, extensionKey, field);
  }

}
