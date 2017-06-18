/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package yacc;


/**
 *
 * @author lgy
 */
public class Yacc {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // String[] texts = { "<postal-address> ::= <name-part> <street-address> <zip-part>",
        //     "<name-part> ::= <personal-part> <last-name> <opt-suffix-part> <EOL> | <personal-part> <name-part>",
        //     "<personal-part> ::= <initial> \".\" | <first-name>",
        //     "<street-address> ::= <house-num> <street-name> <opt-apt-num> <EOL>",
        //     "<zip-part> ::= <town-name> \",\" <state-code> <ZIP-code> <EOL>",
        //     "<opt-suffix-part> ::= \"Sr.\" | \"Jr.\" | <roman-numeral> | \"\"", "<opt-apt-num> ::= <apt-num> | \"\"" };
//        String[] texts = {
//            "<S>::=<i><E><t><S><S'>|<a>",
//            "<S'>::=<e><S>|\"\"",
//            "<E>::=<b>"
//        };
//        String[] testText = {
//            "<a>"
//        };
//        Analysis test = new Analysis(texts);
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
//        test.setFirstSet();
//        test.setFollowSet();
//        System.out.println(test.isLL1());
//        HashMap<String, HashMap<String, ArrayList<String[]>>> ll1Table = test.setLL1Table();
//        Iterator itr = ll1Table.keySet().iterator();
//        while (itr.hasNext()) {
//            String key1 = itr.next().toString();
//            HashMap<String, ArrayList<String[]>> tempMap = ll1Table.get(key1);
//            Iterator _itr = tempMap.keySet().iterator();
//            while (_itr.hasNext()) {
//                String key2 = _itr.next().toString();
//                ArrayList<String[]> list = tempMap.get(key2);
//                System.out.println("row: " + key1 + " => " + "column: " + key2);
//                for (int i = 0; i < list.size(); i++) {
//                    for (int j = 0; j < list.get(i).length; j++) {
//                        System.out.print(list.get(i)[j] + " ");
//                    }
//                    System.out.println();
//                }
//                // System.out.println();
//            }
//        }
//        ArrayList<String> testArr = new ArrayList<>();
//        for(int i =0; i < testText.length; i ++) {
//            testArr.add(testText[i]);
//        }
//        System.out.println(test.runTest(testArr));
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
        TestScript testscript = new TestScript();
        testscript.start("./testcase");
    }

}
