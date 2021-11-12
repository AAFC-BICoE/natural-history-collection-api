package ca.gc.aafc.collection.api.security;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import ca.gc.aafc.dina.security.PermissionAuthorizationService;

@Service
public class CollectionAuthorizationService extends PermissionAuthorizationService {

  @Override
  @PreAuthorize("hasMinimumGroupAndRolePermissions(@currentUser, 'COLLECTION_MANAGER', #entity)")
  public void authorizeCreate(Object entity) {    
  }

  @Override
  @PreAuthorize("hasMinimumGroupAndRolePermissions(@currentUser, 'COLLECTION_MANAGER', #entity)")
  public void authorizeUpdate(Object entity) {    
  }

  @Override
  @PreAuthorize("hasMinimumGroupAndRolePermissions(@currentUser, 'COLLECTION_MANAGER', #entity)")
  public void authorizeDelete(Object entity) {    
  }

  @Override
  public String getName() {
    return "MinimumGroupAndRoleAuthorizationService";
  }
  
}
