package foo.zongzhe.acc.entity;

import java.util.ArrayList;

/**
 * 记录与交易有关的字段，仅选取汇总时的必要字段。
 */
public class Transaction {
    private String transDate; // 交易时间
    private String transAbstract; // 摘要
    private double incomePrice; // 收入金额
    private double outcomePrice; // 支出金额

    public Transaction() {
        transDate = "";
        transAbstract = "";
        incomePrice = 0.00;
        outcomePrice = 0.00;
    }

    public Transaction(String transDate, String transAbstract, String accType) {
        this.transDate = transDate;
        this.transAbstract = transAbstract;
        incomePrice = 0.00;
        outcomePrice = 0.00;
    }

    public Transaction(String transDate, String transAbstract, double incomePrice, double outcomePrice) {
        this.transDate = transDate;
        this.transAbstract = transAbstract;
        this.incomePrice = incomePrice;
        this.outcomePrice = outcomePrice;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransAbstract() {
        return transAbstract;
    }

    public void setTransAbstract(String transAbstract) {
        this.transAbstract = transAbstract;
    }

    public double getIncomePrice() {
        return incomePrice;
    }

    public void setIncomePrice(double incomePrice) {
        this.incomePrice = incomePrice;
    }

    public double getOutcomePrice() {
        return outcomePrice;
    }

    public void setOutcomePrice(double outcomePrice) {
        this.outcomePrice = outcomePrice;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transDate='" + transDate + '\'' +
                ", transAbstract='" + transAbstract + '\'' +
                ", incomePrice=" + incomePrice +
                ", outcomePrice=" + outcomePrice +
                '}';
    }

    /*
    private String accNum;  // 账号
    private String accName; // 户名
    private String voucherType; // 凭证种类
    private String voucherNum; // 凭证号码
    private String busiNum; // 企业业务编号
    private double price; // 金额
    private String currency; // 币种
    private double balance; // 余额
    private String contPartyAccNum; // 对方账号
    private String contPartyAccName; // 对方户名
    private String contPartyBankName; // 对方行名
    private String leBoFlag; // 借贷标志
    private String contPartyBankNum; // 卡号
    private String seqNum; // 流水号

    public Transaction() {
    }

    public Transaction(String accNum, String accName, String transDate, String transAbstract, String voucherType,
                       String voucherNum, String busiNum, double price, String currency, double balance,
                       String contPartyAccNum, String contPartyAccName, String contPartyBankName, String leBoFlag,
                       String contPartyBankNum, String seqNum) {
        this.accNum = accNum;
        this.accName = accName;
        this.transDate = transDate;
        this.transAbstract = transAbstract;
        this.voucherType = voucherType;
        this.voucherNum = voucherNum;
        this.busiNum = busiNum;
        this.price = price;
        this.currency = currency;
        this.balance = balance;
        this.contPartyAccNum = contPartyAccNum;
        this.contPartyAccName = contPartyAccName;
        this.contPartyBankName = contPartyBankName;
        this.leBoFlag = leBoFlag;
        this.contPartyBankNum = contPartyBankNum;
        this.seqNum = seqNum;
    }

    public String getAccNum() {
        return accNum;
    }

    public void setAccNum(String accNum) {
        this.accNum = accNum;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getTransAbstract() {
        return transAbstract;
    }

    public void setTransAbstract(String transAbstract) {
        this.transAbstract = transAbstract;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void setVoucherType(String voucherType) {
        this.voucherType = voucherType;
    }

    public String getVoucherNum() {
        return voucherNum;
    }

    public void setVoucherNum(String voucherNum) {
        this.voucherNum = voucherNum;
    }

    public String getBusiNum() {
        return busiNum;
    }

    public void setBusiNum(String busiNum) {
        this.busiNum = busiNum;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getContPartyAccNum() {
        return contPartyAccNum;
    }

    public void setContPartyAccNum(String contPartyAccNum) {
        this.contPartyAccNum = contPartyAccNum;
    }

    public String getContPartyAccName() {
        return contPartyAccName;
    }

    public void setContPartyAccName(String contPartyAccName) {
        this.contPartyAccName = contPartyAccName;
    }

    public String getContPartyBankName() {
        return contPartyBankName;
    }

    public void setContPartyBankName(String contPartyBankName) {
        this.contPartyBankName = contPartyBankName;
    }

    public String getLeBoFlag() {
        return leBoFlag;
    }

    public void setLeBoFlag(String leBoFlag) {
        this.leBoFlag = leBoFlag;
    }

    public String getContPartyBankNum() {
        return contPartyBankNum;
    }

    public void setContPartyBankNum(String contPartyBankNum) {
        this.contPartyBankNum = contPartyBankNum;
    }

    public String getSeqNum() {
        return seqNum;
    }

    public void setSeqNum(String seqNum) {
        this.seqNum = seqNum;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "accNum='" + accNum + '\'' +
                ", accName='" + accName + '\'' +
                ", transDate='" + transDate + '\'' +
                ", transAbstract='" + transAbstract + '\'' +
                ", price=" + price +
                ", leBoFlag='" + leBoFlag + '\'' +
                '}';
    }
    */
}
