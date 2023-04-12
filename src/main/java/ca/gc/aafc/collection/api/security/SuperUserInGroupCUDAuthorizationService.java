package ca.gc.aafc.collection.api.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.gc.aafc.dina.security.auth.PermissionAuthorizationService;

/**
 * Authorization service that will authorize SUPER_USER (and DINA_ADMIN) for the group of the entity.
 * The currentUser must be at least SUPER_USER on the group defined on the entity.
 */
@Service
public class SuperUserInGroupCUDAuthorizationService extends PermissionAuthorizationService {

  @Override
  @PreAuthorize("hasMinimumGroupAndRolePermissions(@currentUser, 'SUPER_USER', #entity)")
  public void authorizeCreate(Object entity) {
  }

  @Override
  @PreAuthorize("hasMinimumGroupAndRolePermissions(@currentUser, 'SUPER_USER', #entity)")
  public void authorizeUpdate(Object entity) {
  }

  @Override
  @PreAuthorize("hasMinimumGroupAndRolePermissions(@currentUser, 'SUPER_USER', #entity)")
  public void authorizeDelete(Object entity) {
  }

  // Do nothing for now
  @Override
  public void authorizeRead(Object entity) {
  }

  @Override
  public String getName() {
    return "SuperUserInGroupCUDAuthorizationService";
  }
}
