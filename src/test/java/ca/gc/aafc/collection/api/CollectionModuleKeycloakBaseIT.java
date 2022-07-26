package ca.gc.aafc.collection.api;

import ca.gc.aafc.dina.testsupport.security.WithMockKeycloakUser;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Same as {@link CollectionModuleBaseIT} but with Keycloak enabled.
 * Used to test in combination with {@link WithMockKeycloakUser}
 */
@SpringBootTest(
        properties = "keycloak.enabled = true"
)
public class CollectionModuleKeycloakBaseIT extends CollectionModuleBaseIT {

}

