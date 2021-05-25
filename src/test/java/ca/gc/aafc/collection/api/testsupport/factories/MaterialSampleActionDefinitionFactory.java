package ca.gc.aafc.collection.api.testsupport.factories;

import java.util.UUID;

import ca.gc.aafc.collection.api.entities.MaterialSampleActionDefinition;
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
          .createdBy("test user");
  }
  
}
