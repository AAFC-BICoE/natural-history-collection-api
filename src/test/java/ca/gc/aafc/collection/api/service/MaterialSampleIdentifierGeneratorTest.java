package ca.gc.aafc.collection.api.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MaterialSampleIdentifierGeneratorTest {

  @Test
  public void testGetNext() {
    MaterialSampleIdentifierGenerator m = new MaterialSampleIdentifierGenerator();

    assertEquals("ABC-A3", m.generateNextIdentifier("ABC-A2"));
    assertEquals("ABC-A03", m.generateNextIdentifier("ABC-A02"));
    assertEquals("ABC-A10", m.generateNextIdentifier("ABC-A09"));
    assertEquals("ABC-95-10", m.generateNextIdentifier("ABC-95-9"));

    assertEquals("ABC-B", m.generateNextIdentifier("ABC-A"));
    assertEquals("ABC-A03AA", m.generateNextIdentifier("ABC-A03Z"));

    // lowercase input should return lowercase output
    assertEquals("ABC-A03ja", m.generateNextIdentifier("ABC-A03iz"));

    // if there is a mix, uppercase will be preserved
    assertEquals("ABC-A-BA", m.generateNextIdentifier("ABC-A-aZ"));
  }
}
