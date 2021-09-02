package ca.gc.aafc.collection.api;

import ca.gc.aafc.collection.api.entities.DescribedDinaEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;

@Entity
@SuperBuilder
@Setter
@Getter
@RequiredArgsConstructor
public class Institution extends DescribedDinaEntity {

}
