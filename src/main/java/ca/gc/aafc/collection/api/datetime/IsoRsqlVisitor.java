package ca.gc.aafc.collection.api.datetime;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.OrNode;
import cz.jirutka.rsql.parser.ast.RSQLVisitor;

import java.util.List;
import java.util.stream.Collectors;

public class IsoRsqlVisitor implements RSQLVisitor<String, String> {

  @Override
  public String visit(AndNode andNode, String s) {
    return null;
  }

  @Override
  public String visit(OrNode orNode, String s) {
    return null;
  }

  @Override
  public String visit(ComparisonNode comparisonNode, String field) {
    if (comparisonNode.getSelector().equalsIgnoreCase(field)) {
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
