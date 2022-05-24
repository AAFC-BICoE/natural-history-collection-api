package ca.gc.aafc.collection.api.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class Protocol extends UserDescribedDinaEntity {

 @AllArgsConstructor
  public enum ProtocolType {
    COLLECTION_EVENT("Collection Event"),
    SPECIMEN_PREPARATION("Specimen Preparation"),
    DNA_EXTRACTION("DNA Extraction"),
    PCR_REACTION("PCR Reaction"),
    SEQ_REACTION("Sequencing Reaction");

    @Getter
    private final String value;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @NotNull
  @NaturalId
  private UUID uuid;

  private String createdBy;

  @Column(insertable = false, updatable = false)
  private OffsetDateTime createdOn;

  @Column(name = "groupname")
  private String group;

  @NotNull
  @Enumerated(EnumType.STRING)
  private ProtocolType type;

  @NotBlank
  @Size(max = 50)
  private String name;

  @Size(max = 5)
  private String version;
  private String description;
  private String steps;
  private String notes;
  private String reference;

  @Size(max = 50)
  private String equipment;

  @Size(max = 50)
  private String forwardPrimerConcentration;

  @Size(max = 50)
  private String reversePrimerConcentration;

  @Size(max = 50)
  private String reactionMixVolume;

  @Size(max = 50)
  private String reactionMixVolumePerTube;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "productid")
  private Product kit;

  @Version
  private Timestamp lastModified;

}