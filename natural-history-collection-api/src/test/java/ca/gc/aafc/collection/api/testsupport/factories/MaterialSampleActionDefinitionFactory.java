package ca.gc.aafc.collection.api.testsupport.factories;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;

import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.FormTemplate;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.MaterialSampleFormComponent;
import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition.TemplateField;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class MaterialSampleActionDefinitionFactory implements TestableEntityFactory<MaterialSampleActionDefinition> {

  @Override
  public MaterialSampleActionDefinition getEntityInstance() {
    return newMaterialSampleActionDefinition().build();
  }
  
  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static MaterialSampleActionDefinition.MaterialSampleActionDefinitionBuilder newMaterialSampleActionDefinition() {
      return MaterialSampleActionDefinition
          .builder()
          .uuid(UUID.randomUUID())
          .name(RandomStringUtils.randomAlphabetic(5))
          .group(RandomStringUtils.randomAlphabetic(5))
          .actionType(MaterialSampleActionDefinition.ActionType.ADD)
          .formTemplates(new HashMap<>(Map.of(MaterialSampleFormComponent.MATERIAL_SAMPLE, FormTemplate.builder()
            .allowNew(true)
            .allowExisting(true)
            .templateFields(new HashMap<>(Map.of("materialSampleName", TemplateField.builder()
              .enabled(true)  
              .defaultValue("test-default-value")
              .build())))
            .build())))
          .createdBy("test user");
  }
  
}
