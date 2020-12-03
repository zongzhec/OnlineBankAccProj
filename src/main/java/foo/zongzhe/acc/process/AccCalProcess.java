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

    public static ArrayList<String> accTypes;

    public AccCalProcess() {
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

    /**
     * 第一次整理 统一格式，在读取文件的时候同时进行
     * 第二次整理 按摘要合并金额
     * 第三次整理 按金额更新摘要
     * 第四次整理 按日期汇总
     *
     * @return
     */
    public ArrayList<AccWithTrans> processAccInfo() {

        // 读取文件，并进行第一次整理
        HashMap<String, ArrayList<Transaction>> rawTranMap = readTransFromRawFile();

        // 第二次整理
        HashMap<String, ArrayList<Transaction>> transGroupedPrice = groupPriceInTrans(rawTranMap);

        // 第三次整理
        updateTransAbs(transGroupedPrice);

        // 第四次整理
        HashMap<String, HashMap<String, Transaction>> transSummary = summaryTrans(transGroupedPrice);

        System.out.println(transSummary);

        return null;
    }

    private HashMap<String, HashMap<String, Transaction>> summaryTrans(HashMap<String, ArrayList<Transaction>> inputTransMap) {
        // First String is accAbbr, second String is date
        HashMap<String, HashMap<String, Transaction>> transMap = new HashMap<>();

        for (String key : inputTransMap.keySet()) {
            HashMap<String, Transaction> dateGroupedTrans = new HashMap<>();
            ArrayList<Transaction> transList = inputTransMap.get(key);
            for (Transaction trans : transList) {
                String dateKey = trans.getTransDate();
                trans.setTransAbstract(trans.getBankAbbr() + trans.getAccType() + trans.getTransAbstract());
                if (dateGroupedTrans.containsKey(dateKey)) {
                    Transaction existingTrans = dateGroupedTrans.get(dateKey);
                    existingTrans.setTransAbstract(existingTrans.getTransAbstract() + "+" + trans.getTransAbstract());
                    existingTrans.setIncomeJbh(existingTrans.getIncomeJbh() + trans.getIncomeJbh());
                    existingTrans.setOutcomeJbh(existingTrans.getOutcomeJbh() + trans.getOutcomeJbh());
                    existingTrans.setIncomeYbh(existingTrans.getIncomeYbh() + trans.getIncomeYbh());
                    existingTrans.setOutcomeYbh(existingTrans.getOutcomeYbh() + trans.getOutcomeYbh());
                } else {
                    dateGroupedTrans.put(dateKey, trans);
                }
            }
            transMap.put(key, dateGroupedTrans);
        }

        return transMap;
    }

    private void updateTransAbs(HashMap<String, ArrayList<Transaction>> transMap) {
        for (ArrayList<Transaction> tranList : transMap.values()) {
            for (Transaction tran : tranList) {
                String transAbs = tran.getTransAbstract();
                double incomePrice = (tran.getIncomeJbh() == 0) ? tran.getIncomeYbh() : tran.getIncomeJbh();
                double outcomePrice = (tran.getOutcomeJbh() == 0) ? tran.getOutcomeYbh() : tran.getOutcomeJbh();
                double validPrice = (incomePrice == 0) ? outcomePrice : incomePrice;
                if (transAbs.contains("ETC")) {
                    String[] abs = transAbs.split("#");
                    transAbs = abs[0] + validPrice + "#" + abs[1];
                } else {
                    transAbs = transAbs + validPrice;
                }
                tran.setTransAbstract(transAbs);
            }

        }

    }

    private HashMap<String, ArrayList<Transaction>> groupPriceInTrans(HashMap<String, ArrayList<Transaction>> inputTranMap) {
        HashMap<String, ArrayList<Transaction>> transGroupedPrice = new HashMap<>();
        for (String key : inputTranMap.keySet()) {
            ArrayList<Transaction> inputTrans = inputTranMap.get(key);

            // Use a hash map to reduce a round of loop
            String deliminator = "_";
            HashMap<String, Transaction> mapToGroupPrice = new HashMap<>();
            for (Transaction inputT : inputTrans) {
                String tempKey = key + deliminator +
                        inputT.getBankAbbr() + deliminator +
                        inputT.getAccType() + deliminator +
                        inputT.getTransDate() + deliminator +
                        inputT.getTransAbstract();
                if (mapToGroupPrice.containsKey(tempKey)) {
                    Transaction groupedT = mapToGroupPrice.get(tempKey);
                    groupedT.setIncomeJbh(groupedT.getIncomeJbh() + inputT.getIncomeJbh());
                    groupedT.setOutcomeJbh(groupedT.getOutcomeJbh() + inputT.getOutcomeJbh());
                    groupedT.setIncomeYbh(groupedT.getIncomeYbh() + inputT.getIncomeYbh());
                    groupedT.setOutcomeYbh(groupedT.getOutcomeYbh() + inputT.getOutcomeYbh());
                } else {
                    mapToGroupPrice.put(tempKey, inputT);
                }
            }

            ArrayList<Transaction> trans = new ArrayList<>(mapToGroupPrice.values());
            transGroupedPrice.put(key, trans);
        }


        return transGroupedPrice;
    }

    /**
     * 从文件中读取Transaction，并进行第一次整理
     *
     * @return
     */
    public HashMap<String, ArrayList<Transaction>> readTransFromRawFile() {
        HashMap<String, ArrayList<Transaction>> transMap = new HashMap<>();


        // 第一步 读取文件信息
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
                    ArrayList<Transaction> rawTranList = processContents(accAbbr, accType, fileContents);
                    if (!transMap.containsKey(accAbbr)) {
                        transMap.put(accAbbr, new ArrayList<>());
                    }
                    transMap.get(accAbbr).addAll(rawTranList);
                }
            }
        }
        return transMap;
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
    public ArrayList<Transaction> processContents(String accAbbr, String accType, String[][] contents) {
        ArrayList<Transaction> transList = new ArrayList<>();
        AccWithTrans summary = new AccWithTrans(accAbbr, accType);
        HashMap<String, Transaction> transMap = summary.getTransMap();

        // 招行的前几行是空的,而交行最后一行是汇总
        SheetMap sheetMap = new SheetMap();
        String bankAbbr = (contents[0][0].isEmpty()) ? SheetMap.BANK_NAME_ZS : SheetMap.BANK_NAME_JT;
        int beginRow = (bankAbbr.equals(SheetMap.BANK_NAME_ZS)) ? 13 : 2;
        int endRow = (bankAbbr.equals(SheetMap.BANK_NAME_ZS)) ? contents.length - 1 : contents.length - 2;

        for (int i = beginRow; i <= endRow; i++) {
            String dateStr = parseDate(bankAbbr, contents[i][sheetMap.getCellMap().get(bankAbbr).get(SheetMap.TRANS_DATE).col]);
            String transAbstract = parseTransAbs(bankAbbr, accType,
                    contents[i][sheetMap.getCellMap().get(bankAbbr).get(SheetMap.TRANS_ABS).col]);
            Double[] prices = parsePrice(bankAbbr,
                    contents[i][sheetMap.getCellMap().get(bankAbbr).get(SheetMap.BL_FLAG).col],
                    contents[i][sheetMap.getCellMap().get(bankAbbr).get(SheetMap.INCOME_PRICE).col],
                    contents[i][sheetMap.getCellMap().get(bankAbbr).get(SheetMap.OUTCOME_PRICE).col]);

            Transaction trans = new Transaction(accAbbr, bankAbbr, accType, dateStr, transAbstract,
                    0.00, 0.00, 0.00, 0.00);
            switch (accType) {
                case SheetMap.ACC_TYPE_JBH:
                    trans.setIncomeJbh(prices[0]);
                    trans.setOutcomeJbh(prices[1]);
                    break;
                case SheetMap.ACC_TYPE_YBH:
                    trans.setIncomeYbh(prices[0]);
                    trans.setOutcomeYbh(prices[1]);
                    break;
                default:
                    System.out.println("未能检测出账户类型");
            }
            transList.add(trans);
        }
        return transList;
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
//        if (transAbs.contains("ETC")) {
//            // ETC扣款#沪ENU543 -> #沪ENU543ETC扣款
//            String licenceNum = transAbs.substring(transAbs.indexOf('#'));
//            transAbs = licenceNum + "ETC扣款";
//        }
////        if (transAbs.contains("手续费")) transAbs = appendBankAndAccType(bankType ,accType,"支付手续费");
//        if (transAbs.contains("手续费")) transAbs = "支付手续费";

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

}
