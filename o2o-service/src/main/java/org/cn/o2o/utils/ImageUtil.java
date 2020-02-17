package org.cn.o2o.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.cn.o2o.dto.ImageHolder;
import org.springframework.web.multipart.commons.CommonsMultipartFile;


import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

public class ImageUtil {
	private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private static final Random r = new Random();
	private static String basePath = Thread.currentThread().getContextClassLoader().getResource("").getPath();

	/**
	 * 将CommonsMultipartFile转换成File
	 * @param cFile
	 * @return
	 */
	public static File transferCommonsMultipartFiletoFile(CommonsMultipartFile cFile) {
		File newFile = new File(cFile.getOriginalFilename());
		try {
			cFile.transferTo(newFile);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newFile;	
	}
	
	/**
	 * 处理略缩图，并返回新生成的图片相对路径
	 * @param targetAddr
	 * @return
	 */
	public static String generateThumbnail(ImageHolder thumbnail, String targetAddr) {
		String realFileName = getRandomFileName();
		String extension = getFileExtension(thumbnail.getImageName());
		makeDirPath(targetAddr);
		String relativeAddr = targetAddr+realFileName+extension;
		File dest = new File(PathUtil.getImgBasePath()+relativeAddr);
		try {
			Thumbnails.of(thumbnail.getImage()).size(200,200)
			.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath+"static/watermark.jpg")), 0.45f)
			.outputQuality(0.8f).toFile(dest);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return relativeAddr;
	}
	
		
	/**
	 * 创建目标路径所涉及到的目录，即/home/work/com/xxx.jpg
	 * 那么home work com这三个文件夹都得自动创建
	 * @param targetAddr
	 */
	private static void makeDirPath(String targetAddr) {
		String realFileParentPath = PathUtil.getImgBasePath()+targetAddr;
		File dirPath = new File(realFileParentPath);
		if(!dirPath.exists()) {
			dirPath.mkdirs();
		}		
	}
	/**
	 * 获取输入文件流的拓展名
	 * @return
	 */
	private static String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf("."));
	}
	/**
	 * 生成随机文件名，当前年月日小时分钟秒数+5位随机数
	 * @return
	 */
	public static String getRandomFileName() {
		// 获取随机的五位数
		int rannum = r.nextInt(89999)+10000;
		String nowTimeStr = sDateFormat.format(new Date());
		return nowTimeStr+rannum;
	}

	/**
	 * storePath是文件路径还是目录路径，
	 * 如果storePath是文件路径则删除该文件，
	 * 如果storePath是目录路径则删除该目录下的所有文件
	 * @param storePath
	 */
	public static void deleteFileOrPath(String storePath) {
		File fileOrPath = new File(PathUtil.getImgBasePath()+storePath);
		if(fileOrPath.exists()) {
			if(fileOrPath.isDirectory()) {
				File files[] = fileOrPath.listFiles();
				for(int i = 0;i<files.length;i++) {
					files[i].delete();
				}		
			}
			fileOrPath.delete();
		}
	}

	public static void main(String[] args) throws IOException {
		System.out.println(basePath+"static/watermark.jpg");
		Thumbnails.of(new File("C:/Users/Admin/Desktop/timg.jpg")).size(200, 200)
				.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath+"static/watermark.jpg")), 0.45f)
				.outputQuality(0.8f).toFile("C:/Users/Admin/Desktop/timgnew.jpg");
	}

	public static String generateNormalImg(ImageHolder thumbnail, String targetAddr) {
		String realFileName = getRandomFileName();
		String extension = getFileExtension(thumbnail.getImageName());
		makeDirPath(targetAddr);
		String relativeAddr = targetAddr+realFileName+extension;
		File dest = new File(PathUtil.getImgBasePath()+relativeAddr);
		try {
			Thumbnails.of(thumbnail.getImage()).size(337,640)
			.watermark(Positions.BOTTOM_RIGHT, ImageIO.read(new File(basePath + "/static/watermark.jpg")), 0.25f)
			.outputQuality(0.9f).toFile(dest);
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
		return relativeAddr;
	}

}
