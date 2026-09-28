package ca.gc.aafc.collection.api.testsupport.factories;

import java.math.BigDecimal;

import ca.gc.aafc.collection.api.entities.Shipment;
import ca.gc.aafc.dina.testsupport.factories.TestableEntityFactory;

public class ShipmentFactory implements TestableEntityFactory<Shipment> {

  @Override
  public Shipment getEntityInstance() {
    return newShipment().build();
  }

  public static Shipment.ShipmentBuilder newShipment() {
    return Shipment.
        builder()
        .value(new BigDecimal("153.56"))
        .currency("CAD")
        .address(newAddress().build());
  }

  public static Shipment.Address.AddressBuilder newAddress() {
    return Shipment.Address.
        builder()
        .receiverName("Receiver Name");
  }

}
