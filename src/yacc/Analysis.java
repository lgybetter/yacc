/*
 * LL1文法分析类
 */
package yacc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;

/**
 *
 * @author lgy
 */
public class Analysis {

    private HashMap<String, Node> bnf = new HashMap(); // 用于保存BNF的文法结构
    private HashMap<String, HashSet<String>> first = new HashMap(); // 用于保存First集的结构
    private HashMap<String, HashSet<String>> follow = new HashMap(); // 用于保存Follow集的结构
    private String start = ""; // 保存第一个开始符号
    private HashSet<String> terminator = new HashSet<>(); // 终结符数组
    private HashSet<String> notTerminator = new HashSet<>(); // 非终结符数组
    private ArrayList<String> mutilChoose = new ArrayList<>(); // 多分支的文法语句
    private HashMap<String, HashMap<String, ArrayList<String[]>>> ll1Table = new HashMap<>(); // LL1表
    private HashMap<String, HashSet<String>> parent = new HashMap(); // 映射由哪些非终结符推到而来

    /**
     * 判断是否为终结符
     * @param key - 判断的字符串
     * @return - 是否为终结符
     */
    public Boolean isTerminator(String key) {
        if (this.bnf.containsKey(key)) {
            return false;
        }
        return true;
    }

    /**
     * 获取由哪些非终结符推到而来
     * @return - 非终结符集合
     */
    public HashMap<String, HashSet<String>> getParent() {
        return this.parent;
    }
    
    /**
     * 获取非终结符的First集
     * @param key - 非终结符
     * @return - First集合
     */
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

    /**
     * 建立First集
     * @return 返回建立成功的First集
     */
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

    /**
     * 建立每个符号由哪些非终结符推导得到
     * @param key
     * @param parent
     */
    private void setParentSet(String key, String parent) {
        if (this.parent.containsKey(key)) {
            this.parent.get(key).add(parent);
        } else {
            HashSet<String> parentSet = new HashSet<>();
            parentSet.add(parent);
            this.parent.put(key, parentSet);
        }
    }

    /**
     * 获得Follow集
     * @param key - 对应的字符串
     * @return Follow集
     */
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
                        } else if (!_key.equals(key)) {
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
        return followSet;
    }

    /**
     * 建立Follow集, 需要先建立First集
     * @return Follow集
     */
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

    /**
     * 进行整个文法结构的分析构造函数
     * @param texts - 要分析的每一条文法规则
     */
    public Analysis(ArrayList<String> texts) {
        this.bnf = new HashMap();
        this.first = new HashMap();
        this.follow = new HashMap();
        this.start = "";
        this.terminator = new HashSet<>();
        this.notTerminator = new HashSet<>();
        this.mutilChoose = new ArrayList<>();
        this.ll1Table = new HashMap<>();
        this.parent = new HashMap();
        this.terminator.add("$");
        for (int i = 0; i < texts.size(); i++) {
            String[] splitArray = texts.get(i).replaceAll(" ", "").split("::=");
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

    /**
     * 判断是否为LL1文法
     * @return 是否为LL1文法
     */
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

    /**
     * 建立LL1预测分析表
     * @return 分析表
     */
    public HashMap<String, HashMap<String, ArrayList<String[]>>> setLL1Table() {
        Iterator itr = this.notTerminator.iterator();
        while (itr.hasNext()) {
            String key = itr.next().toString();
            Node node = this.bnf.get(key);
            ArrayList<String[]> childs = node.getChilds();
            for (int i = 0; i < childs.size(); i++) {
                if (this.isTerminator(childs.get(i)[0])) {
                    if (childs.get(i)[0].equals("ε")) {
                        HashSet<String> parentSet = this.parent.get(key);
                        Iterator _itr = parentSet.iterator();
                        while (_itr.hasNext()) {
                            String parentKey = _itr.next().toString();
                            HashSet<String> tempFollowSet = this.follow.get(parentKey);
                            Iterator __itr = tempFollowSet.iterator();
                            while (__itr.hasNext()) {
                                String _key = __itr.next().toString();
                                if (!_key.equals("ε")) {
                                    this.insertLL1Table(key, _key, childs.get(i));
                                }
                            }
                        }
                    } else if (!childs.get(i)[0].equals("ε")) {
                        this.insertLL1Table(key, childs.get(i)[0], childs.get(i));
                    }
                } else {
                    HashSet<String> tempFirstSet = this.first.get(childs.get(i)[0]);
                    if (tempFirstSet.contains("ε")) {
                        HashSet<String> parentSet = this.parent.get(key);
                        Iterator _itr = parentSet.iterator();
                        while (_itr.hasNext()) {
                            HashSet<String> tempFollowSet = this.follow.get(_itr.next().toString());
                            Iterator __itr = tempFollowSet.iterator();
                            while (__itr.hasNext()) {
                                String _key = __itr.next().toString();
                                if (_key.equals("ε")) {
                                    this.insertLL1Table(key, _key, childs.get(i));
                                }
                            }
                        }
                    } else {
                        Iterator _itr = tempFirstSet.iterator();
                        while (_itr.hasNext()) {
                            String tempKey = _itr.next().toString();
                            this.insertLL1Table(key, tempKey, childs.get(i));
                        }
                    }
                }
            }
        }
        return this.ll1Table;
    }

    /**
     * 分析表的过程中需要进行建立映射
     * @param key - 非终结符
     * @param key2 - 对应终结符
     * @param value - 对应的推导关系
     */
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

    /**
     * 获得有多分支的文法规则的数组
     * @param key
     * @return 
     */
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

    /**
     * 结合LL1分析预测表进行测试分析，判断用例是否符合文法规则
     * @param texts - 待分析的语句
     * @return 返回成功(true)或者失败(false)
     */
    public Boolean runTest(ArrayList<String> texts) {
        for (int i = 0; i < texts.size(); i++) {
            String tempStr = texts.get(i).replaceAll("<|>|\"", "");
            if (tempStr.length() <= 0) {
                texts.set(i, "ε");
            } else {
                texts.set(i, tempStr);
            }
        }
        texts.add("$");
        Stack<String> stack = new Stack<>();
        stack.push("$");
        stack.push(this.start);
        String x = stack.peek();
        HashMap<String, ArrayList<String[]>> tempMap = null;
        ArrayList<String[]> tempList = null;
        int ip = 0;
        while (!x.equals("$")) {
            Boolean tableExist = true;
            if (this.ll1Table.containsKey(x)) {
                tempMap = this.ll1Table.get(x);
                if (!tempMap.containsKey(texts.get(ip))) {
                    tableExist = false;
                } else {
                    tempList = tempMap.get(texts.get(ip));
                    if (tempList.size() > 1) {
                        tableExist = false;
                    }
                }
            } else {
                tableExist = false;
            }
            if (x.equals(texts.get(ip))) {
                stack.pop();
                ip++;
            } else if (this.isTerminator(x)) {
                return false;
            } else if (!tableExist) {
                return false;
            } else {
                stack.pop();
                String[] tempArray = tempList.get(0);
                for (int i = tempArray.length - 1; i >= 0; i--) {
                    stack.push(tempArray[i]);
                }
            }
            x = stack.peek();
        }
        return true;
    }
}
