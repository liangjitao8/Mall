package org.cn.o2o.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Random;

import static org.cn.o2o.utils.PathUtil.getImgBasePath;

public class FileUtil {
    private static String seperator = System.getProperty("file.separator");
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat(
            "yyyyMMddHHmmss"); // 时间格式化的格式
    private static final Random r = new Random();

    public static String getHeadLineImagePath() {
        String headLineImagePath = "/upload/images/item/headtitle/";
        headLineImagePath = headLineImagePath.replace("/", seperator);
        return headLineImagePath;
    }

    public static String getShopCategoryImagePath() {
        String shopCategoryImagePath = "/upload/images/item/shopcategory/";
        shopCategoryImagePath = shopCategoryImagePath.replace("/", seperator);
        return shopCategoryImagePath;
    }

    public static String getPersonInfoImagePath() {
        String personInfoImagePath = "/upload/images/item/personinfo/";
        personInfoImagePath = personInfoImagePath.replace("/", seperator);
        return personInfoImagePath;
    }

    public static String getShopImagePath(long shopId) {
        StringBuilder shopImagePathBuilder = new StringBuilder();
        shopImagePathBuilder.append("/upload/images/item/shop/");
        shopImagePathBuilder.append(shopId);
        shopImagePathBuilder.append("/");
        String shopImagePath = shopImagePathBuilder.toString().replace("/",
                seperator);
        return shopImagePath;
    }
    public static void deleteFile(String storePath) {
        File file = new File(getImgBasePath() + storePath);
        if (file.exists()) {
            if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            file.delete();
        }
    }
}
