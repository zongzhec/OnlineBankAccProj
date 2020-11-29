package foo.zongzhe.acc.entity;

import java.util.ArrayList;
import java.util.HashMap;

public class AccWithTrans {
    private String accAbbr; // 账户缩写
    private String accType; // 户种：基本户、一般户、贷款户、理财户、社保户、现金、借款户
    private HashMap<String, Transaction> transMap;

    public AccWithTrans(String accAbbr, String accType) {
        this.accAbbr = accAbbr;
        this.accType = accType;
        transMap = new HashMap<>();
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public String getAccAbbr() {
        return accAbbr;
    }

    public void setAccAbbr(String accAbbr) {
        this.accAbbr = accAbbr;
    }

    public HashMap<String, Transaction> getTransMap() {
        return transMap;
    }

    public void setTransMap(HashMap<String, Transaction> transMap) {
        this.transMap = transMap;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();

        res.append("AccWithTrans(").append(accAbbr).append(", ").append(accType).append("):\n");

        for (String key : transMap.keySet()) {
            res.append("key=").append(key).append(", ").append(transMap.get(key)).append("\n");
        }

        return res.toString();
    }
}
