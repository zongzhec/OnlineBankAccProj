package foo.zongzhe.acc.process;

import foo.zongzhe.acc.controller.Controller;
import foo.zongzhe.acc.entity.AccSummary;
import foo.zongzhe.acc.entity.SheetMap;
import foo.zongzhe.acc.entity.Transaction;
import foo.zongzhe.acc.helper.SystemHelper;
import foo.zongzhe.utils.common.DateUtil;
import foo.zongzhe.utils.file.ExcelUtil;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class AccCalProcess {

    private static ArrayList<Transaction> transactions;
    private HashMap<String, HashMap<String, ArrayList<Transaction>>> transMap;
    public static ArrayList<String> accTypes;

    public AccCalProcess() {
        transMap = Controller.transMap;
        if (accTypes == null || accTypes.size() == 0) {
            loadAccTypes();
        }
    }

    public static void loadAccTypes() {
        accTypes = new ArrayList<>();
        accTypes.add("基本户");
        accTypes.add("一般户");
        accTypes.add("贷款户");
        accTypes.add("理财户");
        accTypes.add("社保户");
        accTypes.add("现金");
        accTypes.add("借款户");
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

                // Assign the account type basing on file name
                String accAbbr = "";
                String accType = "";
                String fileName = file.getName();

                for (String aType : accTypes) {
                    if (fileName.contains(aType)) {
                        accType = aType;
                        break;
                    }
                }
                accAbbr = fileName.substring(0, fileName.indexOf(accType));
                System.out.println("accType:" + accType + ", accAbbr: " + accAbbr);

                String[][] fileContents = readFileContent(file);
                if (fileContents == null || fileContents.length <= 2) {
                    System.out.println(file + " 中没有内容，跳过处理此文件");
                } else {
                    processContents(accAbbr, accType, fileContents);
                }
            }
        }
    }

    public String[][] readFileContent(File file) {
        String[][] contents = null;
        String path = file.getPath();
        System.out.println("Reading " + path);
        try {
            contents = ExcelUtil.readExcelValues(path, 0);
        } catch (IOException e) {
            e.printStackTrace();
            SystemHelper.exit("遇到未知错误，请联系维护人员。");
        }
        System.out.println(contents.length);
        return contents;
    }

    /**
     * Process contents basing on different account types.
     *
     * @param contents
     */
    public void processContents(String accAbbr, String accType, String[][] contents) {
        AccSummary summary = new AccSummary(accAbbr, accType);
        ArrayList<Transaction> transList = summary.getTransactions();

        // 招行的前几行是空的,而交行最后一行是汇总
        SheetMap sheetMap = new SheetMap();
        String bankType = (contents[0][0].isEmpty()) ? SheetMap.BANK_NAME_ZS : SheetMap.BANK_NAME_JT;
        int beginRow = (bankType.equals(SheetMap.BANK_NAME_ZS)) ? 13 : 2;
        int endRow = (bankType.equals(SheetMap.BANK_NAME_ZS)) ? contents.length - 1 : contents.length - 2;

        for (int i = beginRow; i <= endRow; i++) {
            String dateStr = parseDate(bankType, contents[i][sheetMap.getCellMap().get(bankType).get(SheetMap.TRANS_DATE).col]);
            String transAbstract = parseTransAbs(bankType, contents[i][sheetMap.getCellMap().get(bankType).get(SheetMap.TRANS_ABS).col]);
            Double[] prices = parsePrice(bankType,
                    contents[i][sheetMap.getCellMap().get(bankType).get(SheetMap.BL_FLAG).col],
                    contents[i][sheetMap.getCellMap().get(bankType).get(SheetMap.INCOME_PRICE).col],
                    contents[i][sheetMap.getCellMap().get(bankType).get(SheetMap.OUTCOME_PRICE).col]);
            transList.add(new Transaction(dateStr, transAbstract, prices[0], prices[1]));
        }

        System.out.println(summary);
    }


    /**
     * Parse dates into MM/dd format to match the summarize sheet.
     *
     * @param bankType
     * @param dateStrInput
     * @return
     */
    private String parseDate(String bankType, String dateStrInput) {
        String fullDateStr = dateStrInput;
        String dateStr = "";
        switch (bankType) {
            case SheetMap.BANK_NAME_ZS: // 20201127
                fullDateStr = dateStrInput.substring(0, 8);
                dateStr = fullDateStr.substring(4, 6) + "/" + fullDateStr.substring(6, 8);
                break;
            case SheetMap.BANK_NAME_JT: // 2020/11/27 11:52
                System.out.println("dateStrInput: " + dateStrInput);
                fullDateStr = dateStrInput.substring(0, 10);
                dateStr = fullDateStr.substring(5, 7)+ "/" + fullDateStr.substring(8, 10);
                break;
            default:
                System.out.println("日期格式无法被识别，请联系维护人员。");
        }
        return dateStr;
    }

    private String parseTransAbs(String bankType, String rawAbstract) {
        String transAbs = rawAbstract;

        if (transAbs.contains("ETC")) transAbs = "ETC";

        return transAbs;
    }


    private Double[] parsePrice(String bankType, String blFlag, String incomeStr, String outcomeStr) {
        Double[] prices = new Double[2];
        incomeStr = incomeStr.replaceAll(",", "");
        outcomeStr = outcomeStr.replaceAll(",", "");

        switch (bankType) {
            case SheetMap.BANK_NAME_ZS: // 招行直接有借金和贷金
                prices[0] = incomeStr.isEmpty() ? 0.00 : Double.parseDouble(incomeStr);
                prices[1] = outcomeStr.isEmpty() ? 0.00 : Double.parseDouble(outcomeStr);
                break;
            case SheetMap.BANK_NAME_JT: // 交行要先看借贷标志
                prices[0] = (blFlag.equals("借")) ? Double.parseDouble(incomeStr) : 0.00;
                prices[1] = (blFlag.equals("贷")) ? Double.parseDouble(outcomeStr) : 0.00;
                break;
            default:
                System.out.println("金额格式无法被识别，请联系维护人员。");
        }

        return prices;
    }

}
