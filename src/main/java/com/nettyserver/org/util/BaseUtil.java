package com.nettyserver.org.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author SuanCaiYv
 * @time 2020/2/15 下午6:34
 */
public class BaseUtil
{
    public static String getUuid()
    {
        return UUID.randomUUID().toString().trim().replace("-", "_");
    }

    /**
     * 强制创建文件
     * @param filePath NA
     * @return NA
     */
    public static File forceCreateFile(String filePath)
    {
        int index = filePath.lastIndexOf("/");
        String dire = filePath.substring(0, index);
        Path direPath = Paths.get(dire);
        Path file = Paths.get(filePath);
        if (!Files.exists(direPath)) {
            try {
                Files.createDirectories(direPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!Files.exists(file)) {
            try {
                Files.createFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.toFile();
    }
}
