/*
 * 测试脚本
 */
package yacc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author lgy
 */
public class TestScript {

    private ArrayList<String> readFileByLines(String fileName) {
        File file = new File(fileName);
        ArrayList<String> resultList = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                resultList.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return resultList;
    }

    private ArrayList<String> traverseFile(String path) {
        File file = new File(path);
        ArrayList<String> list = new ArrayList<String>();
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (!file2.isDirectory()) {
                    list.add(file2.toString());
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        return list;
    }

    private ArrayList<String> traverseFolder(String path) {
        File file = new File(path);
        ArrayList<String> list = new ArrayList<String>();
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File file2 : files) {
                if (file2.isDirectory()) {
                    list.add(file2.toString());
                }
            }
        } else {
            System.out.println("文件不存在!");
        }
        return list;
    }

    public void start(String path) {
        ArrayList<String> floders = this.traverseFolder(path);
        for (int i = 0; i < floders.size(); i++) {
            int floderNum = i + 1;
            ArrayList<String> files = this.traverseFile(floders.get(i));
            String inputBnf = floders.get(i) + "/input.bnf";
            ArrayList<String> bnf = this.readFileByLines(inputBnf);
            try {
                Analysis analysis = new Analysis(bnf);
                analysis.setFirstSet();
                analysis.setFollowSet();
                System.out.println("测试BNF用例 =>" + floders.get(i) + ": ");
                Boolean isLL1 = analysis.isLL1();
                System.out.println("\t" + "是否为LL1文法: " + isLL1);
                analysis.setLL1Table();
                if (isLL1) {
                    System.out.println("\t\t" + "LL1文法测试: ");
                    for (int j = 0; j < files.size() - 1; j++) {
                        int num = j + 1;
                        String file = floders.get(i) + "/tokenstream" + num + ".tok";
                        ArrayList<String> testText = this.readFileByLines(file);
                        Boolean result = analysis.runTest(testText);
                        System.out.println("\t\t\t" + "文法测试用例 =>" + file + " 测试结果 : " + result);
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                continue;
            }

        }
    }
}
