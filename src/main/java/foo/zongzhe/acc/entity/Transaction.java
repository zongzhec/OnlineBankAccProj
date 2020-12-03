package foo.zongzhe.acc.entity;

/**
 * 记录与交易有关的字段，仅选取汇总时的必要字段。
 */
public class Transaction {
    private String accAbbr; // 户名缩写
    private String bankAbbr; // 银行缩写
    private String accType; // 账户类型
    private String transDate; // 交易时间
    private String transAbstract; // 摘要
    private Double incomeJbh; // 基本户收入
    private Double outcomeJbh; // 基本户支出
    private Double incomeYbh; // 一般户收入
    private Double outcomeYbh; // 一般户支出

    public Transaction() {
        transDate = "";
        transAbstract = "";
        incomeJbh = 0.00;
        outcomeJbh = 0.00;
    }

    public Transaction(String accAbbr, String bankAbbr, String accType, String transDate, String transAbstract,
                       Double incomeJbh, Double outcomeJbh, Double imcomeYbh, Double outcomeYbh) {
        this.accAbbr = accAbbr;
        this.bankAbbr = bankAbbr;
        this.accType = accType;
        this.transDate = transDate;
        this.transAbstract = transAbstract;
        this.incomeJbh = incomeJbh;
        this.outcomeJbh = outcomeJbh;
        this.incomeYbh = imcomeYbh;
        this.outcomeYbh = outcomeYbh;
    }

    public Transaction(String transDate, String transAbstract, double incomeJbh, double outcomePrice) {
        this.transDate = transDate;
        this.transAbstract = transAbstract;
        this.incomeJbh = incomeJbh;
        this.outcomeJbh = outcomePrice;
    }

    public String getTransDate() {
        return transDate;
    }

    public void setTransDate(String transDate) {
        this.transDate = transDate;
    }

    public String getAccAbbr() {
        return accAbbr;
    }

    public String getAccType() {
        return accType;
    }

    public void setAccType(String accType) {
        this.accType = accType;
    }

    public void setAccAbbr(String accAbbr) {
        this.accAbbr = accAbbr;
    }

    public String getBankAbbr() {
        return bankAbbr;
    }

    public void setBankAbbr(String bankAbbr) {
        this.bankAbbr = bankAbbr;
    }

    public void setIncomeJbh(Double incomeJbh) {
        this.incomeJbh = incomeJbh;
    }

    public void setOutcomeJbh(Double outcomeJbh) {
        this.outcomeJbh = outcomeJbh;
    }

    public String getTransAbstract() {
        return transAbstract;
    }

    public void setTransAbstract(String transAbstract) {
        this.transAbstract = transAbstract;
    }

    public double getIncomeJbh() {
        return incomeJbh;
    }

    public void setIncomeJbh(double incomeJbh) {
        this.incomeJbh = incomeJbh;
    }

    public double getOutcomeJbh() {
        return outcomeJbh;
    }

    public void setOutcomeJbh(double outcomeJbh) {
        this.outcomeJbh = outcomeJbh;
    }

    public Double getIncomeYbh() {
        return incomeYbh;
    }

    public void setIncomeYbh(Double incomeYbh) {
        this.incomeYbh = incomeYbh;
    }

    public Double getOutcomeYbh() {
        return outcomeYbh;
    }

    public void setOutcomeYbh(Double outcomeYbh) {
        this.outcomeYbh = outcomeYbh;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "accAbbr='" + accAbbr + '\'' +
                ", bankAbbr='" + bankAbbr + '\'' +
                ", transDate='" + transDate + '\'' +
                ", transAbstract='" + transAbstract + '\'' +
                ", incomeJbh=" + incomeJbh +
                ", outcomeJbh=" + outcomeJbh +
                ", imcomeYbh=" + incomeYbh +
                ", outcomeYbh=" + outcomeYbh +
                '}';
    }
}
