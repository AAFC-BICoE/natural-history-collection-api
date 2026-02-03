package ca.gc.aafc.collection.api.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.UUID;
import javax.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import ca.gc.aafc.collection.api.dto.CollectionManagedAttributeDto;
import ca.gc.aafc.collection.api.dto.SiteDto;
import ca.gc.aafc.collection.api.service.SiteService;
import ca.gc.aafc.collection.api.testsupport.ServiceTransactionWrapper;
import ca.gc.aafc.collection.api.testsupport.factories.SiteFactory;
import ca.gc.aafc.collection.api.testsupport.fixtures.SiteTestFixture;
import ca.gc.aafc.dina.exception.ResourceGoneException;
import ca.gc.aafc.dina.exception.ResourceNotFoundException;
import ca.gc.aafc.dina.jsonapi.JsonApiDocument;
import ca.gc.aafc.dina.jsonapi.JsonApiDocuments;
import ca.gc.aafc.dina.testsupport.jsonapi.JsonAPITestHelper;
import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import ca.gc.aafc.collection.api.entities.Site;

public class SiteRepositoryIT extends BaseRepositoryIT {
    @Inject
    private SiteRepository siteRepository;

    @Inject
    private SiteService siteService;

    @Inject
    protected ServiceTransactionWrapper serviceTransactionWrapper;

    @Test
    @WithMockKeycloakUser(username = "dev", groupRole = { "aafc:user" })
    public void create_WithAuthenticatedUser_SetsCreatedBy()
            throws ResourceGoneException, ResourceNotFoundException {
        SiteDto siteDto = SiteTestFixture.newSite();
        UUID siteUUID = createWithRepository(siteDto, siteRepository::onCreate);

        SiteDto result = siteRepository.getOne(siteUUID, "").getDto();

        assertNotNull(result.getCreatedBy());
        assertEquals(siteDto.getName(), result.getName());
        assertEquals(siteDto.getGroup(), result.getGroup());
    }

    @Test
    @WithMockKeycloakUser(username = "other user", groupRole = { "notAAFC:user" })
    public void updateFromDifferentGroup_throwAccessDenied()
            throws ResourceGoneException, ResourceNotFoundException {
        Site testSite = SiteFactory.newSite()
                .group("preparation process definition")
                .name("aafc")
                .build();
        serviceTransactionWrapper.execute(siteService::create, testSite);

        SiteDto retrievedSite = siteRepository.getOne(testSite.getUuid(), "").getDto();
        JsonApiDocument docToUpdate = JsonApiDocuments.createJsonApiDocument(
                retrievedSite.getUuid(), CollectionManagedAttributeDto.TYPENAME,
                JsonAPITestHelper.toAttributeMap(retrievedSite));

        assertThrows(AccessDeniedException.class, () -> siteRepository.onUpdate(docToUpdate, docToUpdate.getId()));
    }
}
