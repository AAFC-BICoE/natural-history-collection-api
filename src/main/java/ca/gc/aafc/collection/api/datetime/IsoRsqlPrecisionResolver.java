package ca.gc.aafc.collection.api.datetime;

import ca.gc.aafc.collection.api.datetime.ISODateTime.Format;
import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Rsql visitor to support rsql date filtering with partial dates. Partial dates are resolved using {@link
 * ISODateTime#parse(String)}
 */
@AllArgsConstructor
public class IsoRsqlPrecisionResolver implements RSQLVisitor<IsoRsqlPrecisionResolver.PrecisionNode, Set<String>> {

  private static final RSQLParser RSQL_PARSER = new RSQLParser();
  private final Map<String, String> fieldToPrecisionNameMap;

  /**
   * Returns the given rsql string with partial dates resolved for fields tracked by the resolver. Partial
   * dates are resolved using {@link * ISODateTime#parse(String)}
   *
   * @param rsql rsql string to resolve
   * @return the given rsql string with partial dates resolved
   */
  public String resolveDates(String rsql) {
    return RSQL_PARSER.parse(rsql).accept(this, fieldToPrecisionNameMap.keySet()).toString();
  }

  @Override
  public PrecisionNode visit(AndNode andNode, Set<String> field) {
    return resolveLogicalNode(andNode, field);
  }

  @Override
  public PrecisionNode visit(OrNode orNode, Set<String> field) {
    return resolveLogicalNode(orNode, field);
  }

  private PrecisionNode resolveLogicalNode(LogicalNode logicalNode, Set<String> field) {
    List<Node> nodes = new ArrayList<>();
    for (Node node : logicalNode.getChildren()) {
      PrecisionNode accept = node.accept(this, field);
      nodes.add(accept.getNode());
    }
    return PrecisionNode.builder()
      .node(logicalNode.withChildren(nodes))
      .build();
  }

  @Override
  public PrecisionNode visit(ComparisonNode node, Set<String> field) {
    if (field.stream().anyMatch(f -> node.getSelector().equalsIgnoreCase(f))) {
      ISODateTime argument = node.getArguments().stream().findFirst().map(ISODateTime::parse).orElseThrow();
      LocalDateTime lowerBound = argument.getLocalDateTime();
      LocalDateTime upperBound = getUpperBound(lowerBound, argument.getFormat());
      if (node.getOperator().equals(RSQLOperators.EQUAL)) {
        return PrecisionNode.builder()
          .node(newBoundedNode(node, lowerBound, upperBound, node.getSelector()))
          .build();
      } else {
        return PrecisionNode.builder()
          .node(new ComparisonNode(node.getOperator(),
            node.getSelector(),
            List.of(argument.getLocalDateTime().toString())))
          .build();
      }
    } else {
      return PrecisionNode.builder().node(node).build();
    }
  }

  private AndNode newBoundedNode(
    ComparisonNode node,
    LocalDateTime lowerBound,
    LocalDateTime upperBound,
    String selector
  ) {
    return new AndNode(List.of(
      new ComparisonNode(
        RSQLOperators.GREATER_THAN_OR_EQUAL,
        selector,
        List.of(lowerBound.toString())),
      new ComparisonNode(
        RSQLOperators.LESS_THAN_OR_EQUAL,
        node.getSelector(),
        List.of(upperBound.toString()))));
  }

  private static LocalDateTime getUpperBound(LocalDateTime lowerBound, Format format) {
    LocalDateTime upperBound = null;
    switch (format) {
      case YYYY:
        upperBound = lowerBound.plusYears(1).minusNanos(1);
        break;
      case YYYY_MM:
        upperBound = lowerBound.plusMonths(1).minusNanos(1);
        break;
      case YYYY_MM_DD:
        upperBound = lowerBound.plusDays(1).minusNanos(1);
        break;
      case YYYY_MM_DD_HH_MM:
        upperBound = lowerBound.plusMinutes(1).minusNanos(1);
        break;
      case YYYY_MM_DD_HH_MM_SS:
        upperBound = lowerBound.plusSeconds(1).minusNanos(1);
        break;
      case YYYY_MM_DD_HH_MM_SS_MMM:
        upperBound = lowerBound;
        break;
    }
    return upperBound;
  }

  @Builder
  @Getter
  public static final class PrecisionNode {
    private final Node node;

    @Override
    public String toString() {
      System.out.println(this.getNode().toString());
      return this.getNode().toString();
    }
  }
}
