package ca.gc.aafc.collection.api;

import java.util.List;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import ca.gc.aafc.collection.api.config.ApiInfoConfiguration;
import ca.gc.aafc.collection.api.entities.DinaDataImport;
import ca.gc.aafc.collection.api.service.DatabaseDataImportService;
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
  private final ApiInfoConfiguration apiInfoConfiguration;

  /**
   * Constructs a new MigrationHintsService with the provided data access object.
   *
   * @param baseDAO the data access object used for database operations
   */
  public StartupChecks(DatabaseDataImportService databaseDataImportService, ApiInfoConfiguration apiInfoConfiguration) {
    this.databaseDataImportService = databaseDataImportService;
    this.apiInfoConfiguration = apiInfoConfiguration;
  }

  @Override
  public void afterSingletonsInstantiated() {
    checkPendingDataImport();
  }

  private void checkPendingDataImport() {
    List<DinaDataImport> pendingImports = databaseDataImportService.findPendingImports();

    if (!pendingImports.isEmpty()) {
      apiInfoConfiguration.setAttentionRequired(true);
    }
  }
}
