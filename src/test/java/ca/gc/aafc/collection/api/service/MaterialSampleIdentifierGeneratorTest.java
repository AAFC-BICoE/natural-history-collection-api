package ca.gc.aafc.collection.api.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaterialSampleIdentifierGeneratorTest {

  @Test
  public void testGetNext() {
    MaterialSampleIdentifierGenerator m = new MaterialSampleIdentifierGenerator();

    assertEquals("ABC-A3", m.getNext("ABC-A2"));
    assertEquals("ABC-A03", m.getNext("ABC-A02"));
    assertEquals("ABC-A10", m.getNext("ABC-A09"));
    assertEquals("ABC-95-10", m.getNext("ABC-95-9"));

  }
}
