package ca.gc.aafc.collection.api.datetime;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Rsql visitor to support rsql date filtering with partial dates. Partial dates are resolved using {@link
 * ISODateTime#parse(String)}
 */
@AllArgsConstructor
public class IsoRsqlPrecisionResolver implements RSQLVisitor<IsoRsqlPrecisionResolver.PrecisionNode, List<String>> {

  private static final RSQLParser RSQL_PARSER = new RSQLParser();
  private final List<String> fieldList;

  /**
   * Returns the given rsql string with partial dates resolved for fields tracked by the resolver. Partial
   * dates are resolved using {@link * ISODateTime#parse(String)}
   *
   * @param rsql rsql string to resolve
   * @return the given rsql string with partial dates resolved
   */
  public String resolveDates(String rsql) {
    return RSQL_PARSER.parse(rsql).accept(this, fieldList).toString();
  }

  @Override
  public PrecisionNode visit(AndNode andNode, List<String> field) {
    List<Node> nodes = new ArrayList<>();
    int highestPrecision = 0;
    for (Node node : andNode.getChildren()) {
      PrecisionNode accept = node.accept(this, field);
      if (accept.getHighestPrecision() != null) {
        highestPrecision = Math.max(highestPrecision, accept.getHighestPrecision());
      }
      nodes.add(accept.getNode());
    }
    return PrecisionNode.builder()
      .node(andNode.withChildren(nodes))
      .highestPrecision(highestPrecision == 0 ? null : highestPrecision)
      .build();
  }

  @Override
  public PrecisionNode visit(OrNode orNode, List<String> field) {
    List<Node> nodes = new ArrayList<>();
    int highestPrecision = 0;
    for (Node node : orNode.getChildren()) {
      PrecisionNode accept = node.accept(this, field);
      if (accept.getHighestPrecision() != null) {
        highestPrecision = Math.max(highestPrecision, accept.getHighestPrecision());
      }
      nodes.add(accept.getNode());
    }
    return PrecisionNode.builder()
      .node(orNode.withChildren(nodes))
      .highestPrecision(highestPrecision == 0 ? null : highestPrecision)
      .build();
  }

  @Override
  public PrecisionNode visit(ComparisonNode node, List<String> field) {
    if (field.stream().anyMatch(f -> node.getSelector().equalsIgnoreCase(f))) {
      List<String> mappedArguments = new ArrayList<>();
      int highestPrecision = 0;
      for (String s : node.getArguments()) {
        ISODateTime dateTime = ISODateTime.parse(s);
        highestPrecision = Math.max(highestPrecision, dateTime.getFormat().getPrecision());
        mappedArguments.add(dateTime.getLocalDateTime().toString());
      }
      return PrecisionNode.builder()
        .node(new ComparisonNode(node.getOperator(), node.getSelector(), mappedArguments))
        .highestPrecision(highestPrecision).build();
    } else {
      return PrecisionNode.builder().node(node).highestPrecision(null).build();
    }
  }

  @Builder
  @Getter
  public static final class PrecisionNode {
    private final Node node;
    private final Integer highestPrecision;

    @Override
    public String toString() {
      return this.getNode().toString();
    }
  }
}
