package ca.gc.aafc.collection.api.testsupport.factories;

import ca.gc.aafc.collection.api.entities.Assemblage;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

import java.util.UUID;

public class AssemblageFactory implements TestableEntityFactory<Assemblage> {

  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static Assemblage.AssemblageBuilder<?, ?> newAssemblage() {
    return Assemblage.builder()
            .group("aafc")
            .name(TestableEntityFactory.generateRandomNameLettersOnly(7))
            .uuid(UUID.randomUUID())
            .createdBy("test user");
  }

  @Override
  public Assemblage getEntityInstance() {
    return newAssemblage().build();
  }
}
