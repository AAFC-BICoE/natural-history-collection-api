package ca.gc.aafc.collection.api.entities;

import javax.persistence.Entity;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import ca.gc.aafc.dina.entity.ControlledVocabulary;

@Entity(name = "controlled_vocabulary")
@SuperBuilder
@RequiredArgsConstructor
public class CollectionControlledVocabulary extends ControlledVocabulary {

}
