package foo.zongzhe.acc.helper;

public class SystemHelper {

    public SystemHelper() {
    }

    public static void exit(String message) {
        System.out.println(message);
        System.out.println("程序即将退出…");
        System.exit(0);
    }
}
