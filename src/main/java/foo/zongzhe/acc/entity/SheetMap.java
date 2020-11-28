package foo.zongzhe.acc.entity;

import java.util.HashMap;

/**
 * Record the content mapping in each sheet for different account types.
 */
public class SheetMap {

    private String bankName; // 交行BCM，招行CMB
    public static HashMap<String, HashMap<String, Cell>> cellMap;

    public static final String BANK_NAME_JT = "JT";
    public static final String BANK_NAME_ZS = "ZS";

    public static final String TRANS_DATE = "TRANS_DATE";
    public static final String TRANS_ABS = "TRANS_ABS";
    public static final String ACC_TYPE = "ACC_TYPE";
    public static final String INCOME_PRICE = "INCOME_PRICE";
    public static final String OUTCOME_PRICE = "OUTCOME_PRICE";
    public static final String BL_FLAG = "BL_FLAG";


    public SheetMap() {
        setupCellMap();
    }

    private void setupCellMap() {
        cellMap = new HashMap<>();
        // 交行BCM
        HashMap<String, Cell> cellMapBCM = new HashMap<>();
        cellMapBCM.put(TRANS_DATE, new Cell(2, 0));
        cellMapBCM.put(TRANS_ABS, new Cell(2, 1));
        cellMapBCM.put(INCOME_PRICE, new Cell(2, 5));
        cellMapBCM.put(OUTCOME_PRICE, new Cell(2, 5));
        cellMapBCM.put(BL_FLAG, new Cell(2, 12));
        cellMap.put(BANK_NAME_JT, cellMapBCM);

        // 招行CMB
        HashMap<String, Cell> cellMapCMB = new HashMap<>();
        cellMapCMB.put(TRANS_DATE, new Cell(13, 0));
        cellMapCMB.put(TRANS_ABS, new Cell(13, 3));
        cellMapCMB.put(INCOME_PRICE, new Cell(13, 4));
        cellMapCMB.put(OUTCOME_PRICE, new Cell(13, 5));
        cellMapCMB.put(BL_FLAG, new Cell(13, 0)); // no such field
        cellMap.put(BANK_NAME_ZS, cellMapCMB);
    }

    public HashMap<String, HashMap<String, Cell>> getCellMap() {
        if (cellMap == null || cellMap.isEmpty()) {
            setupCellMap();
        }
        return cellMap;
    }
}
