package ca.gc.aafc.collection.api.entities;

import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.UniqueElements;
import org.javers.core.metamodel.annotation.Value;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class Protocol extends UserDescribedDinaEntity {

  @Size(max = 50)
  @Column
  private String protocolType;

  @NotBlank
  @Size(max = 50)
  @Column(name = "_group")
  private String group;

  @Type(type = "list-array")
  @Column(name = "attachments", columnDefinition = "uuid[]")
  @UniqueElements
  private List<UUID> attachments = List.of();

  @Type(type = "jsonb")
  @Column(name = "protocol_data", columnDefinition = "jsonb")
  @Valid
  private List<ProtocolData> protocolData = List.of();

  @Data
  @Builder
  @Value
  public static class ProtocolData {

    @NotBlank
    @Size(max = 50)
    private String key; // forward_primer

    private boolean vocabularyBased;

    private List<ProtocolDataElement> protocolDataElement;
  }

  @Data
  @Builder
  @Value
  public static class ProtocolDataElement {

    private boolean vocabularyBased;

    @NotBlank
    @Size(max = 250)
    private String elementType; // concentration, reaction quantity

    @NotBlank
    @Size(max = 250)
    private String value;

    private String unit;
  }
}
