package ca.gc.aafc.collection.api.mixs;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;

import org.apache.commons.lang3.StringUtils;

import ca.gc.aafc.collection.api.mixs.FieldExtensionDefinition.Extension;
import ca.gc.aafc.collection.api.mixs.FieldExtensionDefinition.Field;
import ca.gc.aafc.dina.workbook.WorkbookConverter;

public final class MIxSTransformer {

  private MIxSTransformer() {
    //not called
  }

  private static final ObjectMapper MAPPER = new ObjectMapper(new YAMLFactory().disable(Feature.WRITE_DOC_START_MARKER))
    .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

  public static void handleMIxSSheetConversion(String extensionName, String extensionKey, String extensionVersion, String fileName, String spreadSheet) throws IOException {
    File file = new File("src/main/resources/extension/spreadsheets/" + spreadSheet);
    Extension extension = Extension.builder()
      .name(extensionName)
      .key(extensionKey)
      .version(extensionVersion)
      .fields(workbookRowToField(WorkbookConverter.convertSheet(new FileInputStream(file), 1)))
      .build();

    FieldExtensionDefinition fieldExtensionDefinition = FieldExtensionDefinition.builder()
      .extension(extension)
      .build();

    MAPPER.writeValue(new File("src/main/resources/extension/" + fileName), fieldExtensionDefinition);
  }

  public static List<Field> workbookRowToField (List<WorkbookConverter.WorkbookRow> workbookRows) {
    List<Field> fieldExtensionDefinitions = new ArrayList<>();
    for (WorkbookConverter.WorkbookRow workbookRow : workbookRows) {
      if (!StringUtils.isBlank(workbookRow.getContent()[0])) {

        Field fieldExtensionDefinition = Field.builder()
          .term(workbookRow.getContent()[0])
          .name(workbookRow.getContent()[1])
          .definition(workbookRow.getContent()[2])
          .dinaComponent("COLLECTING_EVENT")
          .build();
        
        fieldExtensionDefinitions.add(fieldExtensionDefinition);
      }
    }
    return fieldExtensionDefinitions;
  }

  public static void main(String[] args) throws IOException {
    handleMIxSSheetConversion(args[0], args[1], args[2], args[3], args[4]);
  }
  
}
