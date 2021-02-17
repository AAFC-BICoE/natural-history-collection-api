package ca.gc.aafc.collection.api.entities;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

public class CollectingEventManagedAttribute {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "event_id")
  CollectingEvent event;

  @ManyToOne
  @JoinColumn(name = "managed_attribute_id")
  ManagedAttribute attribute;

}
