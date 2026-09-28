package ca.gc.aafc.collection.api.testsupport.fixtures;

import java.math.BigDecimal;
import java.time.LocalDate;

import ca.gc.aafc.collection.api.entities.Shipment;

public class ShipmentTestFixture {

  public static final String CURRENCY = "CAD";

  private ShipmentTestFixture() {
  }

  public static Shipment.ShipmentBuilder newShipment() {
    return Shipment.builder()
        .contentRemarks("2 spiders")
        .packingMethod("box")
        .value(new BigDecimal("23.56"))
        .currency(CURRENCY)
        .trackingNumber("CVT-846549387")
        .status("Pick by Post Office")
        .shippedOn(LocalDate.now())
        .itemCount(2)
        .address(newAddress().build())
        .shipmentRemarks("Fragile");
  }

  public static Shipment.Address.AddressBuilder newAddress() {
    return Shipment.Address.builder()
        .receiverName("The Receiver")
        .addressLine1("123 Street")
        .city("City")
        .provinceState("QC")
        .country("Canada")
        .zipCode("H0H 0H0");
  }

}
