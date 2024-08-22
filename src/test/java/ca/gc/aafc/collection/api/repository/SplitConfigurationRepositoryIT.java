package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.CollectionModuleKeycloakBaseIT;
import ca.gc.aafc.collection.api.dto.ProjectDto;
import ca.gc.aafc.collection.api.dto.SplitConfigurationDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.SplitConfigurationTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.crnk.core.queryspec.QuerySpec;
import javax.inject.Inject;

public class SplitConfigurationRepositoryIT extends CollectionModuleKeycloakBaseIT {

  @Inject
  private SplitConfigurationRepository splitConfigurationRepository;

  @Test
  @WithMockKeycloakUser(username = "dev",
    groupRole = {SplitConfigurationTestFixture.GROUP + ":SUPER_USER"})
  public void create_WithAuthenticatedUser_SetsCreatedBy() {
    SplitConfigurationDto splitConfigurationDto =
      SplitConfigurationTestFixture.newSplitConfiguration();

    SplitConfigurationDto result = splitConfigurationRepository.findOne(
      splitConfigurationRepository.create(splitConfigurationDto).getUuid(),
      new QuerySpec(ProjectDto.class));

    assertNotNull(result.getCreatedBy());
    assertNotNull(result.getConditionalOnMaterialSampleTypes());
    assertEquals(splitConfigurationDto.getName(), result.getName());
    assertEquals(splitConfigurationDto.getGroup(), result.getGroup());
  }
}
