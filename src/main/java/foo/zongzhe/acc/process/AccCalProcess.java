package foo.zongzhe.acc.process;

import foo.zongzhe.acc.controller.Controller;
import foo.zongzhe.acc.entity.Transaction;
import foo.zongzhe.acc.helper.SystemHelper;
import foo.zongzhe.utils.file.ExcelUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class AccCalProcess {

    private static ArrayList<Transaction> transactions;
    private HashMap<String, HashMap<String, ArrayList<Transaction>>> transMap;

    public AccCalProcess() {
        transMap = Controller.transMap;
    }

    public void readAndStoreInfo() {
// Read all files under srcDirPath
        File srcDir = new File(Controller.srcDirPath);
        File[] files = srcDir.listFiles();
        if (files == null || files.length == 0) {
            SystemHelper.exit(Controller.srcDirPath + " 中没有任何文件可读取");
        } else {
            for (File file : files) {
                if (!file.isDirectory()) System.out.println(file);
                readFileContent(file);
            }
        }
    }

    public void readFileContent(File file) {
        String[][] contents = null;
        try {
            System.out.println("file.getPath(): " + file.getPath());
            contents = ExcelUtil.readExcelValues(file.getPath(), 0);
        } catch (IOException e) {
            e.printStackTrace();
            SystemHelper.exit("遇到未知错误，请联系维护人员。");
        }
        if (contents == null || contents.length <= 2) return;

        System.out.println(contents.length);

        // First line is Account info
        String accNum = contents[0][1];
        String accName = contents[0][3];
        // Outer key is comp name, inner key is date
        if (!transMap.containsKey(accNum)) {
            transMap.put(accNum, new HashMap<>());
        }
        HashMap<String, ArrayList<Transaction>> transInThisComp = transMap.get(accNum);

        // Second line is title, and the last line is sum-up, hence we read from line 3 to length-2
        for (int i = 3; i < contents.length - 1; i++) {
            String[] lineInfo = contents[i];
            String dateStr = lineInfo[0].substring(0, 10);
            Transaction trans = new Transaction(accNum, accName, dateStr, lineInfo[1],
                    lineInfo[2], lineInfo[3], lineInfo[4],
                    Double.parseDouble(lineInfo[5].replaceAll(",", "")), lineInfo[6],
                    Double.parseDouble(lineInfo[7].replaceAll(",", "")), lineInfo[8],
                    lineInfo[9], lineInfo[10], lineInfo[11], lineInfo[12], lineInfo[13]);
            System.out.println(trans);
            transactions.add(trans);

            // Outer key is comp name, inner key is date
            if (!transInThisComp.containsKey(dateStr)) {
                transInThisComp.put(dateStr, new ArrayList<>());
            }

            transInThisComp.get(dateStr).add(trans);
        }


    }

}
