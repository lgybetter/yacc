import java.util.*;

public class Yacc {
  public static class Node {
    private String name;
    private ArrayList<String[]> childs = new ArrayList();

    public Node(String name) {
      this.name = name;
    }

    public ArrayList<String[]> getChilds() {
      return this.childs;
    }

    public void split(String childs) {
      String[] childArray = childs.split("\\|");
      String [] temp = {"ε"};
      for (int i = 0; i < childArray.length; i++) {
        String[] test = childArray[i].split("><|\"<|>\"|\"\"");
        if(test.length <= 0) {
          test = temp;
        }
        for (int j = 0; j < test.length; j++) {
          test[j] = test[j].replaceAll("<|>", "");
          test[j] = test[j].replaceAll("\"", "");
        }
        this.childs.add(test);
      }
    }
  }

  public static class Analysis {
    private HashMap<String, Node> bnf = new HashMap();
    private HashMap<String, HashSet<String>> first = new HashMap();
    private HashMap<String, HashSet<String>> follow = new HashMap();

    public Boolean isTerminator(String key) {
      if (this.bnf.containsKey(key)) {
        return false;
      }
      return true;
    }

    public HashSet<String> getFirstSet(String key) {
      Node node = this.bnf.get(key);
      ArrayList<String[]> childs = node.getChilds();
      HashSet<String> firstSet = new HashSet<>();
      for (int i = 0; i < childs.size(); i++) {
        for (int j = 0; j < childs.get(i).length; j++) {
          if (this.isTerminator(childs.get(i)[j])) {
            if (childs.get(i)[j] != "ε") {
              firstSet.add(childs.get(i)[j]);
              break;
            }
          } else {
            HashSet<String> tempFirstSet = this.getFirstSet(childs.get(i)[j]);
            Iterator itr = tempFirstSet.iterator();
            while (itr.hasNext()) {
              firstSet.add(itr.next().toString());
            }
            break;
          }
        }
      }
      return firstSet;
    }

    // public HashMap<String, ArrayList<String>> setFollowSet() {

    // }

    public HashMap<String, HashSet<String>> setFirstSet() {
      Iterator iter = this.bnf.keySet().iterator();
      while (iter.hasNext()) {
        String key = iter.next().toString();
        HashSet<String> value = this.getFirstSet(key);
        this.first.put(key, value);
      }
      return this.first;
    }

    public Analysis(String[] texts) {
      for (int i = 0; i < texts.length; i++) {
        String[] splitArray = texts[i].replaceAll(" ", "").split("::=");
        Node node = new Node(splitArray[0].replaceAll("<|>", ""));
        node.split(splitArray[1]);
        this.bnf.put(splitArray[0].replaceAll("<|>", ""), node);
      }
      // Iterator iter = this.bnf.keySet().iterator();
      // while (iter.hasNext()) {
      //   String key = iter.next().toString();
      //   System.out.println(key + " : ");
      //   Node node = this.bnf.get(key);
      //   ArrayList<String[]> childs = node.getChilds();
      //   for (int i = 0; i < childs.size(); i++) {
      //     System.out.print(i + " : ");
      //     for (int j = 0; j < childs.get(i).length; j++) {
      //       System.out.print(childs.get(i)[j] + " ");
      //     }
      //     System.out.println();
      //   }
      // }
    }
  }

  public static void main(String[] args) {
    String[] texts = { "<postal-address> ::= <name-part> <street-address> <zip-part>",
        "<name-part> ::= <personal-part> <last-name> <opt-suffix-part> <EOL> | <personal-part> <name-part>",
        "<personal-part> ::= <initial> \".\" | <first-name>",
        "<street-address> ::= <house-num> <street-name> <opt-apt-num> <EOL>",
        "<zip-part> ::= <town-name> \",\" <state-code> <ZIP-code> <EOL>",
        "<opt-suffix-part> ::= \"Sr.\" | \"Jr.\" | <roman-numeral> | \"\"", "<opt-apt-num> ::= <apt-num> | \"\"" };
    Analysis test = new Analysis(texts);
    // HashMap<String, ArrayList<String>> a = test.setFirstSet();
    // Iterator iter = a.keySet().iterator();
    // while(iter.hasNext()) {
    //   String key = iter.next().toString();
    //   System.out.println(key + " : ");
    //   ArrayList<String> firstSet = a.get(key);
    //   for(int i = 0; i < firstSet.size(); i++) {
    //     System.out.print(firstSet.get(i) + " , ");
    //   }
    //   System.out.println();
    // }

    HashSet<String> a = test.getFirstSet("postal-address");
    Iterator itr = a.iterator();
    while(itr.hasNext()) {
      System.out.println(itr.next().toString());
    }
  }
}