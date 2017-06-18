/*
 * 文法节点保存结构
 */
package yacc;

import java.util.ArrayList;

/**
 *
 * @author lgy
 */
public class Node {
    private String name;
    private ArrayList<String[]> childs = new ArrayList();

    public Node(String name) {
      this.name = name;
    }

    public Node(String name, ArrayList<String[]> childs) {
      this.name = name;
      this.childs = childs;
    }

    public ArrayList<String[]> getChilds() {
      return this.childs;
    }

    public void split(String childs) {
      String[] childArray = childs.split("\\|");
      String[] temp = { "ε" };
      for (int i = 0; i < childArray.length; i++) {
        String[] test = childArray[i].split("><|\"<|>\"|\"\"");
        if (test.length <= 0) {
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
