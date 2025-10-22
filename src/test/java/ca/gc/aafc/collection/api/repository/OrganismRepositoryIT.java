package ca.gc.aafc.collection.api.repository;

import org.junit.jupiter.api.Test;

import ca.gc.aafc.collection.api.dto.OrganismDto;
import ca.gc.aafc.collection.api.testsupport.fixtures.DeterminationFixture;
import ca.gc.aafc.collection.api.testsupport.fixtures.OrganismTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.repository.JsonApiModelAssistant;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.MalformedURLException;
import java.util.UUID;
import javax.inject.Inject;

public class OrganismRepositoryIT extends BaseRepositoryIT {

  @Inject
  private OrganismRepository organismRepository;

  @Test
  @WithMockKeycloakUser(groupRole = { OrganismTestFixture.GROUP + ":user" })
  public void create_WithAuthenticatedUser_SetsCreatedBy()
      throws MalformedURLException, ResourceGoneException, ResourceNotFoundException {
    OrganismDto organismDto = OrganismTestFixture.newOrganism(DeterminationFixture.newDetermination());

    JsonApiDocument organismToCreate = JsonApiDocuments.createJsonApiDocument(
      null, OrganismDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(organismDto)
    );

    UUID organismUUID =
      JsonApiModelAssistant.extractUUIDFromRepresentationModelLink(organismRepository
        .onCreate(organismToCreate));

    OrganismDto result = organismRepository.getOne(organismUUID, null).getDto();
    assertNotNull(result.getCreatedBy());
    organismRepository.onDelete(organismUUID);
  }

  @WithMockKeycloakUser(groupRole = {OrganismTestFixture.GROUP + ":user"})
  @Test
  public void create_withUserProvidedUUID_resourceCreatedWithProvidedUUID()
      throws MalformedURLException, ResourceGoneException, ResourceNotFoundException {
    UUID myUUID = UUID.randomUUID();
    OrganismDto organismDto = OrganismTestFixture.newOrganism(DeterminationFixture.newDetermination());
    organismDto.setUuid(myUUID);

    JsonApiDocument organismToCreate = JsonApiDocuments.createJsonApiDocument(
      myUUID, OrganismDto.TYPENAME,
      JsonAPITestHelper.toAttributeMap(organismDto)
    );
    organismRepository.onCreate(organismToCreate);

    var refreshedCe = organismRepository.getOne(myUUID, null);
    assertNotNull(refreshedCe);
  }

}
