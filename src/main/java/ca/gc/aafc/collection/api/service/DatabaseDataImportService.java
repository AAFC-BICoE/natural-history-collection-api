package ca.gc.aafc.collection.api.service;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Service;

import ca.gc.aafc.dina.jpa.BaseDAO;

/**
* Service for querying data import tracking records.
*
* <p>Provides access to pending imports recorded in the
* {@code dina_data_import} table.</p>
*/
@Service
public class DatabaseDataImportService {

  private final BaseDAO baseDAO;

  public DatabaseDataImportService(BaseDAO baseDAO) {
    this.baseDAO = baseDAO; 
  }


  /**
   * Retrieves all import entries that have not yet been processed.
   *
   * @return list of pending import entries
   */
  public List<DatabaseDataImportEntry> findPendingImports() {

    String jpql = """
        select new %s(
        d.id,
        d.sourceDatabase,
        d.sourceSchema,
        d.sourceTable,
        d.targetTable,
        d.status,
        d.processedOn
        )
        from DinaDataImport d
        where d.status is null
        """.formatted(DatabaseDataImportEntry.class.getName());

    return baseDAO.findAllByQuery(
        DatabaseDataImportEntry.class,
        jpql, null);
  }

    /**
   * Projection of a data import tracking record.
   *
   * @param id record identifier
   * @param sourceDatabase source database name
   * @param sourceSchema source schema name
   * @param sourceTable source table name
   * @param targetTable target table name
   * @param status import status; {@code null} indicates the import has not yet
   *               been processed
   * @param processedOn timestamp when processing completed
   */
  public record DatabaseDataImportEntry(
      Integer id,
      String sourceDatabase,
      String sourceSchema,
      String sourceTable,
      String targetTable,
      String status,
      Instant processedOn
  ) {}

}
