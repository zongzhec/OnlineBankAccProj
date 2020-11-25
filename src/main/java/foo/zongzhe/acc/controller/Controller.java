package foo.zongzhe.acc.controller;

public class Controller {
    public static void main(String[] args) {
        Controller controller = new Controller();
        controller.showWelcomeMessage();
    }

    public void showWelcomeMessage() {
        System.out.println("欢迎使用网银交易日记账自动计算系统，请联系18217038586获取验证码");
    }
}
