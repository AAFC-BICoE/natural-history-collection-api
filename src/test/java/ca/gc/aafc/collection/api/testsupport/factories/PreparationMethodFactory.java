package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.PreparationMethod;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

import java.util.UUID;

public class PreparationMethodFactory implements TestableEntityFactory<PreparationMethod> {

  @Override
  public PreparationMethod getEntityInstance() {
    return newPreparationMethod().build();
  }

    /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
    public static PreparationMethod.PreparationMethodBuilder<?, ?> newPreparationMethod() {
      return PreparationMethod
        .builder()
        .uuid(UUID.randomUUID())
        .createdBy("test user");
    }

}
