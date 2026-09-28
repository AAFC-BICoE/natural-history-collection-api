-- Simulates the data imported by the dina-db-init-container from the loan_transaction database into the import tables
INSERT INTO loan_transaction_transaction (id, uuid, transaction_number, other_identifiers, material_direction, material_to_be_returned,
  _group, purpose, transaction_type, status, opened_date, closed_date, due_date, remarks, created_by, created_on,
  shipment, managed_attributes, agent_roles, attachment, material_samples)
VALUES
  (5, '01928a4d-0000-7000-8000-000000000001', 'LT-1', ARRAY['other-1'], 'OUT', true,
   'aafc', 'research', 'loan', 'open', '2024-01-02', NULL, '2024-12-31', 'first', 'loan-user', '2024-01-02T10:00:00Z',
   '{"status": "shipped", "address": {"city": "Ottawa"}}', '{"loan_ma_1": "abc", "loan_ma_2": "1.5"}',
   '[{"agent": "01928a4d-0000-7000-8000-0000000000a1", "roles": ["borrower"]}]',
   ARRAY['01928a4d-0000-7000-8000-0000000000b1']::uuid[], ARRAY['01928a4d-0000-7000-8000-0000000000c1']::uuid[]),
  (9, '01928a4d-0000-7000-8000-000000000002', 'LT-2', NULL, 'IN', false,
   'cnc', NULL, NULL, NULL, NULL, NULL, NULL, NULL, 'loan-user', '2024-02-02T10:00:00Z',
   NULL, '{}', NULL, NULL, NULL);

-- loan_ma_1 is duplicated (different group), the oldest one must be kept
INSERT INTO loan_transaction_managed_attribute (id, uuid, _group, type, name, accepted_values, multilingual_description, key, created_on, created_by)
VALUES
  (1, '01928a4d-0000-7000-8000-000000000011', 'aafc', 'STRING', 'loan ma 1', ARRAY['abc', 'def'],
   '{"descriptions": [{"lang": "en", "desc": "loan ma 1"}]}', 'loan_ma_1', '2024-01-01T10:00:00Z', 'loan-user'),
  (2, '01928a4d-0000-7000-8000-000000000012', 'cnc', 'STRING', 'loan ma 1', NULL,
   NULL, 'loan_ma_1', '2024-03-01T10:00:00Z', 'loan-user'),
  (3, '01928a4d-0000-7000-8000-000000000013', 'aafc', 'DECIMAL', 'loan ma 2', NULL,
   NULL, 'loan_ma_2', '2024-01-01T11:00:00Z', 'loan-user');

INSERT INTO loan_transaction_jv_commit (commit_pk, author, commit_date, commit_date_instant, commit_id)
VALUES
  (1, 'loan-user', '2024-01-02 10:00:00', '2024-01-02T10:00:00Z', 1.00),
  (2, 'loan-user', '2024-01-03 10:00:00', '2024-01-03T10:00:00Z', 2.00);

INSERT INTO loan_transaction_jv_commit_property (property_name, property_value, commit_fk)
VALUES ('agent', 'loan-user', 1);

INSERT INTO loan_transaction_jv_global_id (global_id_pk, local_id, fragment, type_name, owner_id_fk)
VALUES
  (1, '"01928a4d-0000-7000-8000-000000000001"', NULL, 'transaction', NULL),
  (2, NULL, 'shipment', 'ca.gc.aafc.transaction.api.entities.Shipment', 1);

INSERT INTO loan_transaction_jv_snapshot (snapshot_pk, type, version, state, changed_properties, managed_type, global_id_fk, commit_fk)
VALUES
  (1, 'INITIAL', 1, '{"shipment": {"valueObject": "ca.gc.aafc.transaction.api.entities.Shipment", "ownerId": {"entity": "transaction", "cdoId": "01928a4d-0000-7000-8000-000000000001"}, "fragment": "shipment"}}',
   '["shipment"]', 'transaction', 1, 1),
  (2, 'INITIAL', 1, '{"status": "shipped"}', '["status"]', 'ca.gc.aafc.transaction.api.entities.Shipment', 2, 1),
  (3, 'UPDATE', 2, '{"remarks": "first"}', '["remarks"]', 'transaction', 1, 2);

UPDATE dina_data_import SET status = 'IMPORTED', processed_on = current_timestamp WHERE source_database = 'loan_transaction';
