package ca.gc.aafc.collection.api.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

// to be replaced by base-api version when available
@Service
public class DinaAdminAuthorizationService {
  @PreAuthorize("hasAdminRole(@currentUser, 'DINA_ADMIN')")
  public void authorize() {
  }
}
