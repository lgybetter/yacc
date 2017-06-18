import java.util.*;

public class Yacc {
  public static class Node {
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

  public static class Analysis {
    private HashMap<String, Node> bnf = new HashMap();
    private HashMap<String, HashSet<String>> first = new HashMap();
    private HashMap<String, HashSet<String>> follow = new HashMap();
    private String start = "";
    private HashSet<String> terminator = new HashSet<>();
    private HashSet<String> notTerminator = new HashSet<>();
    private ArrayList<String> mutilChoose = new ArrayList<>();
    private HashMap<String, HashMap<String, ArrayList<String[]>>> ll1Table = new HashMap<>();
    private HashMap<String, HashSet<String>> parent = new HashMap();
    public Boolean isTerminator(String key) {
      if (this.bnf.containsKey(key)) {
        return false;
      }
      return true;
    }

    public HashMap<String, HashSet<String>> getParent () {
      return this.parent;
    }

    public HashSet<String> getFirstSet(String key) {
      Node node = this.bnf.get(key);
      ArrayList<String[]> childs = node.getChilds();
      if (childs.size() > 1) {
        this.mutilChoose.add(key);
      }
      HashSet<String> firstSet = new HashSet<>();
      for (int i = 0; i < childs.size(); i++) {
        for (int j = 0; j < childs.get(i).length; j++) {
          if (this.isTerminator(childs.get(i)[j])) {
            // if (childs.get(i)[j] != "ε") {
            firstSet.add(childs.get(i)[j]);
            break;
            // }
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

    public HashMap<String, HashSet<String>> setFirstSet() {
      Iterator iter = this.bnf.keySet().iterator();
      while (iter.hasNext()) {
        String key = iter.next().toString();
        if (!this.first.containsKey(key)) {
          HashSet<String> value = this.getFirstSet(key);
          this.first.put(key, value);
        }
      }
      return this.first;
    }

    private void setParentSet (String key, String parent) {
      if(this.parent.containsKey(key)) {
        this.parent.get(key).add(parent);
      } else {
        HashSet<String> parentSet = new HashSet<>();
        parentSet.add(parent);
        this.parent.put(key, parentSet);
      }
    }

    public HashSet<String> getFollowSet(String key) {
      HashSet<String> followSet = new HashSet<>();
      Iterator itr = this.bnf.keySet().iterator();
      while (itr.hasNext()) {
        String _key = itr.next().toString();
        Node node = this.bnf.get(_key);
        ArrayList<String[]> childs = node.getChilds();
        for (int i = 0; i < childs.size(); i++) {
          for (int j = 0; j < childs.get(i).length; j++) {
            //设置能推出改childs.get(i)[j]的字符
            this.setParentSet(childs.get(i)[j], _key);
            if (childs.get(i)[j].equals(key)) {
              if (j < childs.get(i).length - 1) {
                if (this.isTerminator(childs.get(i)[j + 1])) {
                  followSet.add(childs.get(i)[j + 1]);
                } else {
                  HashSet<String> tempFollowSet = this.first.get(childs.get(i)[j + 1]);
                  Iterator _itr = tempFollowSet.iterator();
                  while (_itr.hasNext()) {
                    followSet.add(_itr.next().toString());
                  }
                }
              } else {
                if (!_key.equals(key)) {
                  if (_key.equals(this.start)) {
                    followSet.add("$");
                  } else {
                    HashSet<String> tempFollowSet = this.getFollowSet(_key);
                    Iterator _itr = tempFollowSet.iterator();
                    while (_itr.hasNext()) {
                      followSet.add(_itr.next().toString());
                    }
                  }
                }
              }
            }
          }
        }
      }
      return followSet;
    }

    public HashMap<String, HashSet<String>> setFollowSet() {
      Iterator iter = this.bnf.keySet().iterator();
      HashSet<String> firstWorldFollowSet = new HashSet<>();
      firstWorldFollowSet.add("$");
      this.follow.put(this.start, firstWorldFollowSet);
      while (iter.hasNext()) {
        String key = iter.next().toString();
        HashSet<String> tempFollowSet = this.getFollowSet(key);
        this.follow.put(key, tempFollowSet);
      }
      return this.follow;
    }

    public Analysis(String[] texts) {
      this.terminator.add("$");
      for (int i = 0; i < texts.length; i++) {
        String[] splitArray = texts[i].replaceAll(" ", "").split("::=");
        Node node = new Node(splitArray[0].replaceAll("<|>", ""));
        node.split(splitArray[1]);
        if (i == 0) {
          this.start = splitArray[0].replaceAll("<|>", "");
        }
        this.bnf.put(splitArray[0].replaceAll("<|>", ""), node);
      }
      Iterator iter = this.bnf.keySet().iterator();
      while (iter.hasNext()) {
        String key = iter.next().toString();
        this.notTerminator.add(key);
        Node node = this.bnf.get(key);
        ArrayList<String[]> childs = node.getChilds();
        for (int i = 0; i < childs.size(); i++) {
          for (int j = 0; j < childs.get(i).length; j++) {
            if (this.isTerminator(childs.get(i)[j])) {
              this.terminator.add(childs.get(i)[j]);
            }
          }
        }
      }
    }

    public Boolean isLL1() {
      for (int i = 0; i < this.mutilChoose.size(); i++) {
        String key = this.mutilChoose.get(i);
        // System.out.println(key);
        ArrayList<HashSet<String>> arr = this.getMultiFirstSet(key);
        // for(int j = 0; j < arr.size(); j++) {
        //   Iterator itr = arr.get(j).iterator();
        //   while(itr.hasNext()) {
        //     System.out.println(itr.next().toString());
        //   }
        // }
        for (int j = 0; j < arr.size(); j++) {
          for (int z = j + 1; z < arr.size(); z++) {
            HashSet<String> result = new HashSet<>();
            result.addAll(arr.get(j));
            result.retainAll(arr.get(z));
            if (result.size() > 0) {
              return false;
            }
          }
        }
        for (int j = 0; j < arr.size(); j++) {
          if (arr.get(j).contains("ε")) {
            HashSet<String> follow = this.follow.get(key);
            for (int z = 0; z < arr.size(); z++) {
              if (j != z) {
                HashSet<String> result = new HashSet<>();
                result.addAll(follow);
                result.retainAll(arr.get(z));
                if (result.size() > 0) {
                  return false;
                }
              }
            }
          }
        }
      }
      return true;
    }

    public HashMap<String, HashMap<String, ArrayList<String[]>>> setLL1Table() {
      Iterator itr = this.notTerminator.iterator();
      while (itr.hasNext()) {
        String key = itr.next().toString();
        Node node = this.bnf.get(key);
        ArrayList<String[]> childs = node.getChilds();
        for (int i = 0; i < childs.size(); i++) {
          if (this.isTerminator(childs.get(i)[0])) {
            if(childs.get(i)[0].equals("ε")) {
              HashSet<String> parentSet = this.parent.get(key);
              Iterator _itr = parentSet.iterator();
              while(_itr.hasNext()) {
                String parentKey = _itr.next().toString();
                HashSet<String> tempFollowSet = this.follow.get(parentKey);
                Iterator __itr = tempFollowSet.iterator();
                while(__itr.hasNext()) {
                  String _key = __itr.next().toString();
                  if(!_key.equals("ε")) {
                    this.insertLL1Table(key, _key, childs.get(i));
                  }
                }
              }
            } else {
              if(!childs.get(i)[0].equals("ε")) {
                this.insertLL1Table(key, childs.get(i)[0], childs.get(i));
              }
            }
          } else {
            HashSet<String> tempFirstSet = this.first.get(childs.get(i)[0]);
            if(tempFirstSet.contains("ε")) {
              HashSet<String> parentSet = this.parent.get(key);
              Iterator _itr = parentSet.iterator();
              while(_itr.hasNext()) {
                HashSet<String> tempFollowSet = this.follow.get(_itr.next().toString());
                Iterator __itr = tempFollowSet.iterator();
                while(__itr.hasNext()) {
                  String _key = __itr.next().toString();
                  if(_key.equals("ε")) {
                    this.insertLL1Table(key, _key, childs.get(i));
                  }
                }
              }
            } else {
              Iterator _itr = tempFirstSet.iterator();
              while (_itr.hasNext()) {
                String tempKey = _itr.next().toString();
                if(tempKey.equals("ε")) {
                  this.insertLL1Table(key, tempKey, childs.get(i));
                }
              }
            }
          }
        }
      }
      return this.ll1Table;
    }

    private void insertLL1Table(String key, String key2, String[] value) {
      if (this.ll1Table.containsKey(key)) {
        HashMap<String, ArrayList<String[]>> map = this.ll1Table.get(key);
        if (map.containsKey(key2)) {
          ArrayList<String[]> tempList = map.get(key2);
          tempList.add(value);
        } else {
          ArrayList<String[]> tempList = new ArrayList<>();
          tempList.add(value);
          map.put(key2, tempList);
        }
      } else {
        HashMap<String, ArrayList<String[]>> map = new HashMap<>();
        ArrayList<String[]> tempList = new ArrayList<>();
        tempList.add(value);
        map.put(key2, tempList);
        this.ll1Table.put(key, map);
      }
    }

    private ArrayList<HashSet<String>> getMultiFirstSet(String key) {
      ArrayList<HashSet<String>> arr = new ArrayList<>();
      Node node = this.bnf.get(key);
      ArrayList<String[]> childs = node.getChilds();
      for (int i = 0; i < childs.size(); i++) {
        HashSet<String> set = new HashSet<>();
        if (this.isTerminator(childs.get(i)[0])) {
          set.add(childs.get(i)[0]);
        } else {
          HashSet<String> tempSet = this.first.get(childs.get(i)[0]);
          Iterator itr = tempSet.iterator();
          while (itr.hasNext()) {
            set.add(itr.next().toString());
          }
        }
        arr.add(set);
      }
      return arr;
    }

    public Boolean runTest (String [] texts) {
      Stack<String> stack = new Stack<>();
      stack.push("$");
      stack.push(this.start);
      String x = stack.peek().toString();
      HashMap<String, ArrayList<String[]>> tempMap = null;
      ArrayList<String[]> tempList = null;
      int ip = 0;
      while (!x.equals("$")) {
        Boolean tableExist = true;
        if(this.ll1Table.containsKey(x)) {
          tempMap = this.ll1Table.get(x);
          if(!tempMap.containsKey(texts[ip])) {
            tableExist = false;
          } else {
            tempList = tempMap.get(texts[ip]);
            if(tempList.size() > 1) {
              tableExist = false;
            }
          }
        } else {
          tableExist = false;
        }
        if(x.equals(texts[ip])) {
          stack.pop();
          ip++;
        } else if (this.isTerminator(x)) {
          return false;
        } else if (!tableExist) {
          return false;
        } else {
          stack.pop();
          String[] tempArray = tempList.get(0);
          for(int i = tempArray.length -1 ; i >= 0; i--) {
            stack.push(tempArray[i]);
          }
        }
        x = stack.peek().toString();
      }
      return true;
    }
  }

  public static void main(String[] args) {
    // String[] texts = { "<postal-address> ::= <name-part> <street-address> <zip-part>",
    //     "<name-part> ::= <personal-part> <last-name> <opt-suffix-part> <EOL> | <personal-part> <name-part>",
    //     "<personal-part> ::= <initial> \".\" | <first-name>",
    //     "<street-address> ::= <house-num> <street-name> <opt-apt-num> <EOL>",
    //     "<zip-part> ::= <town-name> \",\" <state-code> <ZIP-code> <EOL>",
    //     "<opt-suffix-part> ::= \"Sr.\" | \"Jr.\" | <roman-numeral> | \"\"", "<opt-apt-num> ::= <apt-num> | \"\"" };
    String[] texts = {
      "<S>::=<i><E><t><S><S'>|<a>",
      "<S'>::=<e><S>|\"\"",
      "<E>::=<b>"
    };
    String[] testText = {
      "a",
      "$"
    };
    Analysis test = new Analysis(texts);
    // HashMap<String, HashSet<String>> a = test.setFirstSet();
    // Iterator iter = a.keySet().iterator();
    // while(iter.hasNext()) {
    //   String key = iter.next().toString();
    //   System.out.println(key + " : ");
    //   Iterator _itr = a.get(key).iterator();
    //   while(_itr.hasNext()) {
    //     System.out.print(_itr.next().toString() + " , ");
    //   }
    //   System.out.println();
    // }

    // HashSet<String> a = test.getFirstSet("opt-apt-num");
    // Iterator itr = a.iterator();
    // while(itr.hasNext()) {
    //   System.out.println(itr.next().toString());
    // }

    test.setFirstSet();
    test.setFollowSet();
    System.out.println(test.isLL1());
    HashMap<String, HashMap<String, ArrayList<String[]>>> ll1Table = test.setLL1Table();
    Iterator itr = ll1Table.keySet().iterator();
    while(itr.hasNext()) {
      String key1 = itr.next().toString();
      HashMap<String, ArrayList<String[]>> tempMap = ll1Table.get(key1);
      Iterator _itr = tempMap.keySet().iterator();
      while(_itr.hasNext()) {
        String key2 = _itr.next().toString();
        ArrayList<String []> list = tempMap.get(key2);
        System.out.println("row: " + key1 + " => " + "column: " + key2);
        for(int i = 0; i < list.size(); i++) {
          for(int j = 0; j < list.get(i).length; j++) {
            System.out.print(list.get(i)[j] + " ");
          }
          System.out.println();
        }
        // System.out.println();
      }
    }
    System.out.println(test.runTest(testText));
    // HashSet<String> a = test.getFollowSet("personal-part");
    // Iterator itr = a.iterator();
    // while(itr.hasNext()) {
    //   System.out.println(itr.next().toString());
    // }
    // System.out.println("-----------------------------------");
    // HashMap<String, HashSet<String>> a = test.setFollowSet();
    // Iterator iter = a.keySet().iterator();
    // while(iter.hasNext()) {
    //   String key = iter.next().toString();
    //   System.out.println(key + " : ");
    //   Iterator _itr = a.get(key).iterator();
    //   while(_itr.hasNext()) {
    //     System.out.print(_itr.next().toString() + " , ");
    //   }
    //   System.out.println();
    // }
  }
}