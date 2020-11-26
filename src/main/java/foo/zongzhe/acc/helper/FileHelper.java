package foo.zongzhe.acc.helper;

import foo.zongzhe.acc.controller.Controller;
import foo.zongzhe.utils.file.FileUtil;

import java.util.ArrayList;

public class FileHelper {

    public FileHelper() {
    }

    public static void checkNeededFiles(){
        Controller.rootDirPath = Controller.properties.getProperty("RootDirPath");
        Controller.srcDirPath = Controller.properties.getProperty("SrcDirPath");
        Controller.destFileName = Controller.properties.getProperty("DestFileName");
        Controller.destFilePath = Controller.rootDirPath + "\\" + Controller.destFileName;

        ArrayList<String> filePaths = new ArrayList<>();
        filePaths.add(Controller.rootDirPath);
        filePaths.add(Controller.srcDirPath);
        filePaths.add(Controller.destFilePath);

        for (String filePath: filePaths){
            if (!FileUtil.fileExists(filePath)){
                SystemHelper.exit("未检测到 " + filePath +", 请准备好此文件");
            }
        }
    }
}
