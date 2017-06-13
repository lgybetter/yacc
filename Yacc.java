import java.util.*;

public class Yacc {
  public static class Node {
    private String name;
    private int type;
    private ArrayList<ArrayList<Node>> child = new ArrayList();
    public Node (String name, int type) {
      this.name = name;
      this.type = type; // 0: 非终结符, 1: 终结符
    }
    public String getName () {
      return this.name;
    }
    public int getType () {
      return this.type;
    }
    public void split (String childs) {
      String[] childArray = childs.split("\\|");
      for(int i = 0; i < childArray.length; i++) {
        String[] test = childArray[i].split("><");
        for(int j = 0; j < test.length; j++) {
          test[j] = test[j].replaceAll("<|>", ""));
        }
      }
    }
  }
  public static void main(String[] args) {
    String text = "<postal-address> ::= <name-part> | <street-address> <zip-part> | \"\"";
    String[] splitArray = text.replaceAll(" ", "").split("::=");
    new Node(splitArray[0].replaceAll("<|>", ""), 1).split(splitArray[1]);
  }
}