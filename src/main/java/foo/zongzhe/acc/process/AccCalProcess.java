package foo.zongzhe.acc.process;

import foo.zongzhe.acc.controller.Controller;
import foo.zongzhe.acc.entity.AccWithTrans;
import foo.zongzhe.acc.entity.SheetMap;
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

    public ArrayList<AccWithTrans> readAndStoreInfo() {
        ArrayList<AccWithTrans> accSummarieList = new ArrayList<>();

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
                    AccWithTrans summary = processContents(accAbbr, accType, fileContents);
                    accSummarieList.add(summary);
                }
            }
        }
        return accSummarieList;
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
    public AccWithTrans processContents(String accAbbr, String accType, String[][] contents) {
        AccWithTrans summary = new AccWithTrans(accAbbr, accType);
        HashMap<String, Transaction> transMap = summary.getTransMap();

        // 招行的前几行是空的,而交行最后一行是汇总
        SheetMap sheetMap = new SheetMap();
        String bankType = (contents[0][0].isEmpty()) ? SheetMap.BANK_NAME_ZS : SheetMap.BANK_NAME_JT;
        int beginRow = (bankType.equals(SheetMap.BANK_NAME_ZS)) ? 13 : 2;
        int endRow = (bankType.equals(SheetMap.BANK_NAME_ZS)) ? contents.length - 1 : contents.length - 2;

        for (int i = beginRow; i <= endRow; i++) {
            String dateStr = parseDate(bankType, contents[i][sheetMap.getCellMap().get(bankType).get(SheetMap.TRANS_DATE).col]);
            String transAbstract = parseTransAbs(bankType, accType,
                    contents[i][sheetMap.getCellMap().get(bankType).get(SheetMap.TRANS_ABS).col]);
            Double[] prices = parsePrice(bankType,
                    contents[i][sheetMap.getCellMap().get(bankType).get(SheetMap.BL_FLAG).col],
                    contents[i][sheetMap.getCellMap().get(bankType).get(SheetMap.INCOME_PRICE).col],
                    contents[i][sheetMap.getCellMap().get(bankType).get(SheetMap.OUTCOME_PRICE).col]);

            Transaction trans = new Transaction(dateStr, transAbstract, prices[0], prices[1]);
            mergeTransIntoMap(bankType, accType, transMap, trans);
        }

        System.out.println(summary);
        return summary;
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
                fullDateStr = dateStrInput.substring(0, 10);
                dateStr = fullDateStr.substring(5, 7) + "/" + fullDateStr.substring(8, 10);
                break;
            default:
                System.out.println("日期格式无法被识别，请联系维护人员。");
        }
        return dateStr;
    }

    private String parseTransAbs(String bankType, String accType, String rawAbstract) {
        String transAbs = rawAbstract;

        // Apply some rules
        if (transAbs.contains("ETC")){
            // ETC扣款#沪ENU543 -> #沪ENU543ETC扣款
            String licenceNum = transAbs.substring(transAbs.indexOf('#'));
            transAbs = licenceNum + "ETC扣款";
        }
//        if (transAbs.contains("手续费")) transAbs = appendBankAndAccType(bankType ,accType,"支付手续费");
        if (transAbs.contains("手续费")) transAbs = "支付手续费";

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
                prices[0] = (blFlag.equals("贷")) ? Double.parseDouble(incomeStr) : 0.00;
                prices[1] = (blFlag.equals("借")) ? Double.parseDouble(outcomeStr) : 0.00;
                break;
            default:
                System.out.println("金额格式无法被识别，请联系维护人员。");
        }

        return prices;
    }

    private void mergeTransIntoMap(String bankType, String accType,
                                   HashMap<String, Transaction> transMap, Transaction transToMerge) {
        Transaction transOrigin = transToMerge;
        String key = transToMerge.getTransDate() + "_" + transToMerge.getTransAbstract();

        // Special treat on key set
//        if (key.contains("ETC")) key = "ETC";

        if (transMap.containsKey(key)) {
            transOrigin = transMap.get(key);
            transOrigin.setIncomePrice(transOrigin.getIncomePrice() + transToMerge.getIncomePrice());
            transOrigin.setOutcomePrice(transOrigin.getOutcomePrice() + transToMerge.getOutcomePrice());

            // Apply some special rules
           /* if (key.equals("ETC")) {
                String transAbs = transToMerge.getTransAbstract();
                String transOriAbs = transOrigin.getTransAbstract();
//                transOriAbs = appendBankAndAccType(bankType, accType, transOriAbs);
                System.out.println("transAbs: " + transAbs);
                String licenceNum = transAbs.substring(transAbs.indexOf('#'));
                // Append the licence num if not done yet
                if (!transOriAbs.contains(licenceNum)) {
                    transOrigin.setTransAbstract(transOriAbs + licenceNum);
                }
            }*/

        }
        transMap.put(key, transOrigin);
    }

    /**
     * Add bank type info and acc type info for the abstract
     *
     * @param bankType
     * @param accType
     * @param transAbs
     * @return
     */
    private String appendBankAndAccType(String bankType, String accType, String transAbs) {
        if (!transAbs.contains(bankType)) {
            transAbs = bankType + accType + transAbs;
        }
        return transAbs;
    }

}
