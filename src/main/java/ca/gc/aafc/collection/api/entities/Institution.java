package ca.gc.aafc.collection.api.entities;

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
public class Institution extends UserDescribedDinaEntity {

}
