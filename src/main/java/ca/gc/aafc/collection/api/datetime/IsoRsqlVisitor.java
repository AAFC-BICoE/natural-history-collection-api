package ca.gc.aafc.collection.api.datetime;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;

import java.util.List;
import java.util.stream.Collectors;

public class IsoRsqlVisitor implements RSQLVisitor<String, List<String>> {

  @Override
  public String visit(AndNode andNode, List<String> field) {
    return andNode.getChildren()
      .stream()
      .map(node -> node.accept(this, field))
      .collect(Collectors.joining(" and "));
  }

  @Override
  public String visit(OrNode orNode, List<String> field) {
    return orNode.getChildren()
      .stream()
      .map(node -> node.accept(this, field))
      .collect(Collectors.joining(" or "));
  }

  @Override
  public String visit(ComparisonNode comparisonNode, List<String> field) {
    if (field.stream().anyMatch(f -> comparisonNode.getSelector().equalsIgnoreCase(f))) {
      List<String> mappedArguments = comparisonNode.getArguments()
        .stream()
        .map(s -> ISODateTime.parse(s).getLocalDateTime().toString())
        .collect(Collectors.toList());
      return new ComparisonNode(comparisonNode.getOperator(), comparisonNode.getSelector(), mappedArguments)
        .toString();
    } else {
      return comparisonNode.toString();
    }
  }
}
