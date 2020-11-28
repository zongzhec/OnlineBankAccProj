package foo.zongzhe.acc.entity;

import java.util.ArrayList;

public class AccSummary {
    private String accAbbr; // 账户缩写
    private String accType; // 户种：基本户、一般户、贷款户、理财户、社保户、现金、借款户

    private ArrayList<Transaction> transactions;

    public AccSummary(String accAbbr, String accType) {
        this.accAbbr = accAbbr;
        this.accType = accType;
        transactions = new ArrayList<>();
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

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transaction> transactions) {
        this.transactions = transactions;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();

        res.append("AccSummary(").append(accAbbr).append(", ").append(accType).append("):\n");

        for (Transaction trans : transactions) {
            res.append(trans).append("\n");
        }

        return res.toString();
    }
}
