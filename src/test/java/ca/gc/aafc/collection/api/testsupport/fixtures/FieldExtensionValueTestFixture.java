package ca.gc.aafc.collection.api.testsupport.fixtures;

import ca.gc.aafc.dina.extension.FieldExtensionDefinition.Field;
import ca.gc.aafc.dina.i18n.MultilingualDescription;
import ca.gc.aafc.collection.api.dto.FieldExtensionValueDto;
import java.util.List;

public class FieldExtensionValueTestFixture {

  public static final String GROUP = "dina";

  public static FieldExtensionValueDto newFieldExtensionValueDto() {
    final String id = "mixs_microbial_v4.alkyl_diethers";
    final String extensionName = "MIxS Microbial v4";
    final String extensionKey = "mixs_microbial_v4";
    final Field field = Field.builder().key("alkyl_diethers").name("alkyl diethers").dinaComponent("COLLECTING_EVENT")
        .multilingualDescription(new MultilingualDescription(List.of(MultilingualDescription
        .MultilingualPair.of("en", "concentration of alkyl diethers ")))).build();
    FieldExtensionValueDto fieldExtensionValueDto = new FieldExtensionValueDto(id, extensionName, extensionKey, field);
    return fieldExtensionValueDto;
  }

}
