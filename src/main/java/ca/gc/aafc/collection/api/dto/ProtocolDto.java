public class CollectionDto extends AttributeMetaInfoProvider { 
 
  @JsonApiId
  @Id
  @PropertyName("id")
  private UUID uuid;

  private String createdBy;

  private OffsetDateTime createdOn;

  private String group;

  private ProtocolType type;

  private String name;

  private String version;
  private String description;
  private String steps;
  private String notes;
  private String reference;

  private String equipment;

  private String forwardPrimerConcentration;

  private String reversePrimerConcentration;

  private String reactionMixVolume;

  private String reactionMixVolumePerTube;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "productid")
  private Product kit;

  @Version
  private Timestamp lastModified;
}