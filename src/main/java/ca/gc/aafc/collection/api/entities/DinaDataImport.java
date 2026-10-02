package ca.gc.aafc.collection.api.entities;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "dina_data_import")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class DinaDataImport {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String sourceDatabase;
  private String sourceSchema;
  private String sourceTable;
  private String targetTable;
  private String status;
  private Instant processedOn;
}
