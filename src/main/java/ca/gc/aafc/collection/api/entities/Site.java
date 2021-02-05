package ca.gc.aafc.collection.api.entities;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.NaturalIdCache;
import org.hibernate.annotations.Type;
import com.vividsolutions.jts.geom.Polygon;

import ca.gc.aafc.dina.entity.DinaEntity;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import com.vividsolutions.jts.geom.Polygon;


@Entity
@AllArgsConstructor
@Builder
@Setter
@Getter
@RequiredArgsConstructor
@SuppressFBWarnings(justification = "ok for Hibernate Entity", value = { "EI_EXPOSE_REP", "EI_EXPOSE_REP2" })
@NaturalIdCache
public class Site implements DinaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NaturalId
    @NotNull
    @Column(unique = true)
    private UUID uuid;

    private String name;
    private String description;

    private Polygon polygon;

    @Column(insertable = false, updatable = false)
    private OffsetDateTime createdOn;
  
    @NotBlank
    @Column(updatable = false)
    private String createdBy;    
}
