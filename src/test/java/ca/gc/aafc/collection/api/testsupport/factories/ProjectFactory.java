package ca.gc.aafc.collection.api.testsupport.factories;

import java.util.UUID;

import ca.gc.aafc.collection.api.entities.Project;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class ProjectFactory implements TestableEntityFactory<Project> {

  /**
   * Static method that can be called to return a configured builder that can be
   * further customized to return the actual entity object, call the .build()
   * method on a builder.
   *
   * @return Pre-configured builder with all mandatory fields set
   */
  public static Project.ProjectBuilder<?, ?> newProject() {
    return Project.builder()
        .group("aafc")
        .name(TestableEntityFactory.generateRandomNameLettersOnly(7))
        .uuid(UUID.randomUUID())
        .createdBy("test user");
  }

  @Override
  public Project getEntityInstance() {
    return newProject().build();
  }
}
