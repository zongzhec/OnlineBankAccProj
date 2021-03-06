package foo.zongzhe.acc.controller;

import foo.zongzhe.acc.entity.AccWithTrans;
import foo.zongzhe.acc.helper.FileHelper;
import foo.zongzhe.acc.process.AccCalProcess;
import foo.zongzhe.utils.security.VerifyUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public class Controller {

    public static Properties properties;
    public static String rootDirPath, srcDirPath, destFileName, destFilePath;
    public static ArrayList<AccWithTrans> accWithTransList;

    public static void main(String[] args) {
        Controller controller = new Controller();

        // Initialize
        controller.initialize();

        // Enter into welcome process
//        controller.checkVerificationCode();

        // Enter into Pre-check process
        controller.preCheck();

        // Enter into main process
        AccCalProcess calProcess = new AccCalProcess();
        accWithTransList = calProcess.processAccInfo();
    }

    public void initialize() {
        InputStream is;
        InputStreamReader isReader;
        properties = new Properties();

        System.out.println("程序加载中…读取配置文件…");
        try {
            is = Controller.class.getResourceAsStream("/properties.properties");
            isReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            properties.load(isReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkVerificationCode() {
        boolean verified = false;
        Scanner scanner = new Scanner(System.in);
        String inputCode;
        System.out.println("欢迎使用网银交易日记账自动计算系统");

        while (!verified) {
            System.out.println("请输入验证码：");
            inputCode = scanner.nextLine().trim();
            verified = VerifyUtil.checkVerificationCode(inputCode);
            if (!verified) System.out.println("验证失败，联系18217038586获取验证码");
        }
        System.out.println("验证成功！");
    }

    /**
     * Pre-checks on:
     * 1. whether all necessary files are in place
     */
    public void preCheck() {
        FileHelper.checkNeededFiles();


    }
}
