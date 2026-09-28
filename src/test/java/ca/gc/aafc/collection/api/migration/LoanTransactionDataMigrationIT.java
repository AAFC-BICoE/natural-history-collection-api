package ca.gc.aafc.collection.api.migration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import ca.gc.aafc.collection.api.CollectionModuleBaseIT;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import jakarta.inject.Inject;
import liquibase.integration.spring.SpringLiquibase;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test the migration of the loan-transaction-api data (changeset 200) from the import tables
 * loaded by the dina-db-init-container (changeset 199).
 */
public class LoanTransactionDataMigrationIT extends CollectionModuleBaseIT {

  private static final String CHANGESET_ID = "200-Migrate_loan_transaction_data";

  // superuser of the PostgresTestContainerInitializer container
  private static final String DB_SUPERUSER = "sa";

  private static final String COLLECTION_ENTITIES_PACKAGE = "ca.gc.aafc.collection.api.entities.";

  private static final String[] IMPORT_TABLES = {"loan_transaction_transaction", "loan_transaction_managed_attribute",
    "loan_transaction_jv_commit", "loan_transaction_jv_commit_property", "loan_transaction_jv_global_id",
    "loan_transaction_jv_snapshot"};

  @Inject
  private SpringLiquibase liquibase;

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Test
  public void migrate_whenDataImported_dataMigratedAndImportTablesDropped() throws Exception {
    try (Connection conn = DriverManager.getConnection(dbUrl, DB_SUPERUSER, DB_SUPERUSER)) {
      // data not imported yet on startup, the changeset should be skipped and not marked as ran
      assertEquals(0, queryLong(conn, "SELECT count(*) FROM databasechangelog WHERE id = '" + CHANGESET_ID + "'"));
      assertEquals(6, queryLong(conn, "SELECT count(*) FROM dina_data_import WHERE source_database = 'loan_transaction' AND status IS NULL"));

      long commitPkBefore = queryLong(conn, "SELECT COALESCE(MAX(commit_pk), 0) FROM jv_commit");
      long globalIdPkBefore = queryLong(conn, "SELECT COALESCE(MAX(global_id_pk), 0) FROM jv_global_id");
      long snapshotPkBefore = queryLong(conn, "SELECT COALESCE(MAX(snapshot_pk), 0) FROM jv_snapshot");

      try {
        insertExistingData(conn);
        long commitPkOffset = queryLong(conn, "SELECT MAX(commit_pk) FROM jv_commit");
        long commitIdOffset = queryLong(conn, "SELECT TRUNC(MAX(commit_id)) FROM jv_commit");
        long globalIdPkOffset = queryLong(conn, "SELECT MAX(global_id_pk) FROM jv_global_id");
        long snapshotPkOffset = queryLong(conn, "SELECT MAX(snapshot_pk) FROM jv_snapshot");

        ScriptUtils.executeSqlScript(conn, new ClassPathResource("loan-transaction-import-data.sql"));

        liquibase.afterPropertiesSet();

        assertEquals(1, queryLong(conn, "SELECT count(*) FROM databasechangelog WHERE id = '" + CHANGESET_ID + "'"));
        for (String importTable : IMPORT_TABLES) {
          assertFalse(tableExists(conn, importTable), importTable + " should be dropped");
        }

        // transactions, uuids are kept and new ids generated after the existing transaction
        assertEquals(3, queryLong(conn, "SELECT count(*) FROM transaction"));
        assertEquals("LT-1|OUT|t|aafc|Ottawa|abc|" +
            "01928a4d-0000-7000-8000-0000000000a1|01928a4d-0000-7000-8000-0000000000b1|01928a4d-0000-7000-8000-0000000000c1",
          queryString(conn, "SELECT concat_ws('|', transaction_number, material_direction, material_to_be_returned, _group, " +
            "shipment->'address'->>'city', managed_attributes->>'loan_ma_1', agent_roles->0->>'agent', attachment[1], material_samples[1]) " +
            "FROM transaction WHERE uuid = '01928a4d-0000-7000-8000-000000000001'"));
        assertEquals("LT-2|IN|f|{}", queryString(conn, "SELECT concat_ws('|', transaction_number, material_direction, " +
            "material_to_be_returned, managed_attributes) FROM transaction WHERE uuid = '01928a4d-0000-7000-8000-000000000002'"));
        assertEquals(2, queryLong(conn, "SELECT count(*) FROM transaction WHERE id > (SELECT id FROM transaction WHERE transaction_number = 'EXISTING')"));

        // managed attributes, the oldest one is kept on duplicate key
        assertEquals(2, queryLong(conn, "SELECT count(*) FROM controlled_vocabulary_item WHERE dina_component = 'LOAN_TRANSACTION'"));
        assertEquals("01928a4d-0000-7000-8000-000000000011|aafc|STRING|loan ma 1|{abc,def}",
          queryString(conn, "SELECT concat_ws('|', cvi.uuid, cvi._group, cvi.vocabulary_element_type, cvi.name, cvi.accepted_values) " +
            "FROM controlled_vocabulary_item cvi JOIN controlled_vocabulary cv ON cv.id = cvi.controlled_vocabulary_id " +
            "WHERE cv.uuid = '01998155-a6f0-7c2f-9fcc-994d74222f9c' AND cvi.dina_component = 'LOAN_TRANSACTION' AND cvi.key = 'loan_ma_1'"));
        assertEquals("DECIMAL", queryString(conn, "SELECT vocabulary_element_type FROM controlled_vocabulary_item " +
            "WHERE dina_component = 'LOAN_TRANSACTION' AND key = 'loan_ma_2'"));

        // audit, primary keys and commit ids are after the existing ones
        assertEquals((commitIdOffset + 1) + ".00|" + (commitIdOffset + 2) + ".00",
          queryString(conn, "SELECT string_agg(commit_id::text, '|' ORDER BY commit_pk) FROM jv_commit WHERE commit_pk > " + commitPkOffset));
        assertEquals("agent|loan-user", queryString(conn,
          "SELECT concat_ws('|', property_name, property_value) FROM jv_commit_property WHERE commit_fk = " + (commitPkOffset + 1)));
        assertEquals((globalIdPkOffset + 1) + "|" + COLLECTION_ENTITIES_PACKAGE + "Shipment", queryString(conn,
          "SELECT concat_ws('|', owner_id_fk, type_name) FROM jv_global_id WHERE global_id_pk = " + (globalIdPkOffset + 2)));
        assertEquals((globalIdPkOffset + 1) + "|" + (commitPkOffset + 2), queryString(conn,
          "SELECT concat_ws('|', global_id_fk, commit_fk) FROM jv_snapshot WHERE snapshot_pk = " + (snapshotPkOffset + 3)));
        assertEquals(COLLECTION_ENTITIES_PACKAGE + "Shipment", queryString(conn,
          "SELECT managed_type FROM jv_snapshot WHERE snapshot_pk = " + (snapshotPkOffset + 2)));
        String state = queryString(conn, "SELECT state FROM jv_snapshot WHERE snapshot_pk = " + (snapshotPkOffset + 1));
        assertTrue(state.contains(COLLECTION_ENTITIES_PACKAGE + "Shipment"));
        assertFalse(state.contains("ca.gc.aafc.transaction.api"));
        assertEquals(1, queryLong(conn, "SELECT count(*) FROM jv_global_id g JOIN jv_snapshot s ON s.global_id_fk = g.global_id_pk " +
          "WHERE g.type_name = 'transaction' AND g.local_id = '\"01928a4d-0000-7000-8000-000000000001\"' AND s.version = 2"));
        assertTrue(queryLong(conn, "SELECT last_value FROM jv_commit_pk_seq") >= commitPkOffset + 2);
        assertTrue(queryLong(conn, "SELECT last_value FROM jv_global_id_pk_seq") >= globalIdPkOffset + 2);
        assertTrue(queryLong(conn, "SELECT last_value FROM jv_snapshot_pk_seq") >= snapshotPkOffset + 3);

        // already ran, nothing should happen
        assertDoesNotThrow(() -> liquibase.afterPropertiesSet());
      } finally {
        cleanup(conn, commitPkBefore, globalIdPkBefore, snapshotPkBefore);
      }
    }
  }

  /**
   * Simulates data already present in collection-api (created before the import) so the ids/offsets are not the initial ones.
   */
  private static void insertExistingData(Connection conn) throws SQLException {
    execute(conn, "INSERT INTO transaction (uuid, transaction_number, material_direction, _group) " +
      "VALUES ('01928a4d-0000-7000-8000-0000000000ff', 'EXISTING', 'IN', 'aafc')");
    execute(conn, "INSERT INTO jv_commit (commit_pk, author, commit_id) " +
      "SELECT COALESCE(MAX(commit_pk), 0) + 10, 'existing', COALESCE(TRUNC(MAX(commit_id)), 0) + 10 FROM jv_commit");
    execute(conn, "INSERT INTO jv_global_id (global_id_pk, local_id, type_name) " +
      "SELECT COALESCE(MAX(global_id_pk), 0) + 10, '\"existing\"', 'existing' FROM jv_global_id");
    execute(conn, "INSERT INTO jv_snapshot (snapshot_pk, type, version, state, managed_type, global_id_fk, commit_fk) " +
      "SELECT COALESCE(MAX(snapshot_pk), 0) + 10, 'INITIAL', 1, '{}', 'existing', " +
      "(SELECT MAX(global_id_pk) FROM jv_global_id), (SELECT MAX(commit_pk) FROM jv_commit) FROM jv_snapshot");
  }

  /**
   * Remove the data added by the test since the database is shared by all the tests.
   * If the migration didn't run, the import is reset (pending) so it can't run later.
   */
  private static void cleanup(Connection conn, long commitPk, long globalIdPk, long snapshotPk) throws SQLException {
    if (tableExists(conn, IMPORT_TABLES[0])) {
      for (String importTable : IMPORT_TABLES) {
        execute(conn, "TRUNCATE " + importTable);
      }
      execute(conn, "UPDATE dina_data_import SET status = NULL, processed_on = NULL WHERE source_database = 'loan_transaction'");
    }
    execute(conn, "DELETE FROM jv_snapshot WHERE snapshot_pk > " + snapshotPk);
    execute(conn, "DELETE FROM jv_commit_property WHERE commit_fk > " + commitPk);
    execute(conn, "DELETE FROM jv_global_id WHERE global_id_pk > " + globalIdPk + " AND owner_id_fk IS NOT NULL");
    execute(conn, "DELETE FROM jv_global_id WHERE global_id_pk > " + globalIdPk);
    execute(conn, "DELETE FROM jv_commit WHERE commit_pk > " + commitPk);
    execute(conn, "DELETE FROM transaction");
    execute(conn, "DELETE FROM controlled_vocabulary_item WHERE dina_component = 'LOAN_TRANSACTION'");
  }

  private static boolean tableExists(Connection conn, String tableName) throws SQLException {
    return queryLong(conn, "SELECT count(*) FROM information_schema.tables WHERE table_name = '" + tableName + "'") > 0;
  }

  private static void execute(Connection conn, String sql) throws SQLException {
    try (Statement stmt = conn.createStatement()) {
      stmt.execute(sql);
    }
  }

  private static long queryLong(Connection conn, String sql) throws SQLException {
    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
      rs.next();
      return rs.getLong(1);
    }
  }

  private static String queryString(Connection conn, String sql) throws SQLException {
    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
      rs.next();
      return rs.getString(1);
    }
  }
}
