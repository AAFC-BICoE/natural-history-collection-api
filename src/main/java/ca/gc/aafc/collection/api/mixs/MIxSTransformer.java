package ca.gc.aafc.collection.api.mixs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import ca.gc.aafc.dina.workbook.WorkbookConverter;

public class MIxSTransformer {

  //ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

  public List<WorkbookConverter.WorkbookRow> handleMIxSSheetConversion(InputStream in) throws IOException {
    return WorkbookConverter.convertSheet(in, 1);
  }

  public List<FieldExtensionDefinition> workbookRowToFieldExtensionDefinition (List<WorkbookConverter.WorkbookRow> workbookRows) {
    List<FieldExtensionDefinition> fieldExtensionDefinitions = new ArrayList<>();
    for (WorkbookConverter.WorkbookRow workbookRow : workbookRows) {
      FieldExtensionDefinition fieldExtensionDefinition = FieldExtensionDefinition.builder()
        .term(workbookRow.getContent()[0])
        .name(workbookRow.getContent()[1])
        .definition(workbookRow.getContent()[2])
        .dinaComponent("COLLECTING_EVENT")
        .build();

      fieldExtensionDefinitions.add(fieldExtensionDefinition);
    }
    return fieldExtensionDefinitions;
  }
  
}
