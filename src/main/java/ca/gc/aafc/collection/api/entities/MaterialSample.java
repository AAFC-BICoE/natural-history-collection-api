package ca.gc.aafc.collection.api.entities;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;

import ca.gc.aafc.dina.entity.DinaEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@NaturalIdCache
@Table(name = "material_sample")
public class MaterialSample implements DinaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
  
    @NaturalId
    @NotNull
    @Column(unique = true)
    private UUID uuid;

    @NotBlank
    @Size(max = 50) 
    @Column(name = "_group")
    private String group;

    @Column(name = "created_on", insertable = false, updatable = false)
    @Generated(value = GenerationTime.INSERT)
    private OffsetDateTime createdOn;

    @NotBlank
    @Column(name = "created_by", updatable = false)
    private String createdBy;  

    @Size(max = 25)  
    private String dwcCatalogNumber;

    @Type(type = "string-array")
    private String[] dwcOtherCatalogNumbers;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private CollectingEvent collectingEvent;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "parent_material_sample_id")
    @ToString.Exclude
    private MaterialSample parentMaterialSample;

    @OneToMany(fetch = FetchType.LAZY,
        mappedBy = "parentMaterialSample")
    private List<MaterialSample> materialSampleChildren = new ArrayList<>();

    @Type(type = "list-array")
    @Column(name = "attachment", columnDefinition = "uuid[]")
    private List<UUID> attachment = new ArrayList<>();

    @Column(name = "material_sample_name")
    private String materialSampleName;

    @ManyToOne
    @ToString.Exclude
    private PreparationType preparationType;

    @Column(name = "prepared_by")
    private UUID preparedBy;

    private LocalDate preparationDate;

}
