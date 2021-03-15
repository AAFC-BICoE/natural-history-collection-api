package ca.gc.aafc.collection.api.datetime;

import ca.gc.aafc.dina.filter.RsqlFilterAdapter;
import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.ComparisonOperator;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLOperators;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Used to resolve Iso Date time partial dates with their associated precisions.
 */
@AllArgsConstructor
public class IsoDateTimeRsqlResolver implements RSQLVisitor<Node, Set<String>>, RsqlFilterAdapter {

  private final Map<String, String> precisionFields;

  @Override
  public Node process(Node node) {
    return node.accept(this, precisionFields.keySet());
  }

  @Override
  public Node visit(AndNode andNode, Set<String> field) {
    return resolveLogicalNode(andNode, field);
  }

  @Override
  public Node visit(OrNode orNode, Set<String> field) {
    return resolveLogicalNode(orNode, field);
  }

  private Node resolveLogicalNode(LogicalNode logicalNode, Set<String> field) {
    return logicalNode.withChildren(logicalNode.getChildren()
      .stream()
      .map(node -> node.accept(this, field))
      .collect(Collectors.toList()));
  }

  @Override
  public Node visit(ComparisonNode node, Set<String> field) {
    String selector = node.getSelector();
    if (isSelectedField(field, selector)) {
      ComparisonOperator operator = node.getOperator();
      ISODateTime argument = node.getArguments().stream().findFirst().map(ISODateTime::parse).orElseThrow();
      List<String> precision = List.of(Byte.toString(argument.getFormat().getPrecision()));
      if (operator.equals(RSQLOperators.EQUAL)) {
        return processEqualOperator(selector, argument, precision);
      } else if (operator.equals(RSQLOperators.LESS_THAN) || operator.equals(RSQLOperators.LESS_THAN_OR_EQUAL)) {
        return processLessThenOperator(selector, argument, precision);
      } else if (operator.equals(RSQLOperators.GREATER_THAN) || operator.equals(RSQLOperators.GREATER_THAN_OR_EQUAL)) {
        return processGreaterThenOperator(selector, argument, precision);
      }
    }
    return node;
  }

  private AndNode processLessThenOperator(String selector, ISODateTime argument, List<String> precision) {
    return null;
  }

  private AndNode processGreaterThenOperator(String selector, ISODateTime argument, List<String> precision) {
    return null;
  }

  private AndNode processEqualOperator(String selector, ISODateTime argument, List<String> precision) {
    return new AndNode(List.of(
      new ComparisonNode(RSQLOperators.EQUAL, selector, List.of(argument.getLocalDateTime().toString())),
      new ComparisonNode(RSQLOperators.EQUAL, precisionFields.get(selector), precision)
    ));
  }

  private static boolean isSelectedField(Set<String> field, String selector) {
    return field.stream().anyMatch(s -> s.equalsIgnoreCase(selector));
  }

}
