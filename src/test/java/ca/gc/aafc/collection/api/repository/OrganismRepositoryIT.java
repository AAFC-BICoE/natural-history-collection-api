package ca.gc.aafc.collection.api.repository;

import ca.gc.aafc.collection.api.dto.CollectingEventDto;
import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.CollectingEventTestFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.DeterminationFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.OrganismTestFixture;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import io.crnk.core.queryspec.QuerySpec;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrganismRepositoryIT extends BaseRepositoryIT {

  @Inject
  private OrganismRepository organismRepository;

  @Test
  @WithMockKeycloakUser(groupRole = { OrganismTestFixture.GROUP + ": staff" })
  public void create_WithAuthenticatedUser_SetsCreatedBy() throws MalformedURLException {
    OrganismDto organismDto = OrganismTestFixture.newOrganism(DeterminationFixture.newDetermination());

    UUID organismUUID = organismRepository.create(organismDto).getUuid();
    OrganismDto result = organismRepository.findOne(organismUUID,
        new QuerySpec(OrganismDto.class));
    assertNotNull(result.getCreatedBy());
    organismRepository.delete(organismUUID);
  }

  @WithMockKeycloakUser(groupRole = {OrganismTestFixture.GROUP + ":staff"})
  @Test
  public void create_withUserProvidedUUID_resourceCreatedWithProvidedUUID() throws MalformedURLException {
    UUID myUUID = UUID.randomUUID();
    OrganismDto organismDto = OrganismTestFixture.newOrganism(DeterminationFixture.newDetermination());
    organismDto.setUuid(myUUID);
    organismRepository.create(organismDto);

    QuerySpec querySpec = new QuerySpec(OrganismDto.class);
    OrganismDto refreshedCe = organismRepository.findOne(myUUID, querySpec);
    assertNotNull(refreshedCe);
  }

}
