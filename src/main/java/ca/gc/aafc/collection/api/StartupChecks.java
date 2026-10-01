package ca.gc.aafc.collection.api;

import java.util.List;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import ca.gc.aafc.collection.api.service.DatabaseDataImportService;
import ca.gc.aafc.collection.api.service.DatabaseDataImportService.DatabaseDataImportEntry;
import lombok.extern.log4j.Log4j2;

/**
 * Checks run once all singletons are instantiated, which is before the embedded
 * web server starts
 * accepting requests. A failing check stops the application before it can be
 * considered ready
 * (e.g. by a Kubernetes/OpenShift readiness probe checking the port).
 */
@Log4j2
@Component
public class StartupChecks implements SmartInitializingSingleton {

  private final DatabaseDataImportService databaseDataImportService;

  /**
   * Constructs a new MigrationHintsService with the provided data access object.
   *
   * @param baseDAO the data access object used for database operations
   */
  public StartupChecks(DatabaseDataImportService databaseDataImportService) {
    this.databaseDataImportService = databaseDataImportService;
  }

  @Override
  public void afterSingletonsInstantiated() {
    checkPendingDataImport();
  }

  private void checkPendingDataImport() {
    List<DatabaseDataImportEntry> pendingImports = databaseDataImportService.findPendingImports();

    if (!pendingImports.isEmpty()) {
      throw new IllegalStateException(
          "Pending imports found: " + pendingImports);
    }
  }
}
