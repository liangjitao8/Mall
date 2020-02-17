package org.cn.o2o.utils;

import java.io.File;

public class PathUtil {
	public static String seperator = System.getProperty("file.separator");
	public static String getImgBasePath() {
		String os = System.getProperty("os.name");
		String basePath="";
		if(os.toLowerCase().startsWith("win")) {
			basePath="D:/projectO2O/";
		}else {
			basePath="/home/image/";
		}
		basePath = basePath.replace("/",seperator);
		return basePath;
	}
	public static String getShopImagePath(long shopId) {
		String imagePath = "/upload/images/item/shop/"+shopId+"/";
		return imagePath.replace("/", seperator);
	}
	public static String getHeadLineImagePath() {
		String headLineImagePath = "/upload/images/item/headtitle/";
		headLineImagePath = headLineImagePath.replace("/", seperator);
		return headLineImagePath;
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
