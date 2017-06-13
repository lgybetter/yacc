import java.util.*;

public class Yacc {
  public static class Node {
    private String name;
    private int type;
    private ArrayList<ArrayList<Node>> child = new ArrayList();
    public Node (String name, int type) {
      this.name = name;
      this.type = type;
    }
    public String getName () {
      return this.name;
    }
    public int getType () {
      return this.type;
    }
    public void split (String name, String childs) {
      System.out.println(name.replaceAll("<|>", ""));
      System.out.println(childs);
      String[] childArray = childs.split("\\|");
      for(int i = 0; i < childArray.length; i++) {
        System.out.println(childArray[i]);
      }
    }
  }
  public static void main(String[] args) {
    String text = "<postal-address> ::= <name-part> | <street-address> <zip-part>";
    String[] splitArray = text.replaceAll(" ", "").split("::=");
    new Node("yacc", 2).split(splitArray[0], splitArray[1]);
  }
}