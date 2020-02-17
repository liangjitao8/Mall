package org.cn.o2o.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import ch.qos.logback.core.util.FileUtil;
import org.cn.o2o.dao.HeadLineDao;
import org.cn.o2o.dto.HeadLineExecution;
import org.cn.o2o.dto.ImageHolder;
import org.cn.o2o.enums.HeadLineStateEnum;
import org.cn.o2o.model.HeadLine;
import org.cn.o2o.utils.ImageUtil;
import org.cn.o2o.utils.PathUtil;
import org.cn.o2o.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Service
public class HeadLineService{
	private static String HLLISTKEY = "headlinelist";
	@Autowired
	private HeadLineDao headLineDao;
	@Autowired
	private RedisUtil redisUtil;

	public List<HeadLine> getHeadLineList(HeadLine headLineCondition)
			throws IOException {
		List<HeadLine> headLineList = null;
		ObjectMapper mapper = new ObjectMapper();
		String key = HLLISTKEY;
		if (headLineCondition.getEnableStatus() != null) {
			key = key + "_" + headLineCondition.getEnableStatus();
		}
		if (!redisUtil.hasKey(key)) {
			headLineList = headLineDao.queryHeadLine(headLineCondition);
			String jsonString = mapper.writeValueAsString(headLineList);
			redisUtil.set(key, jsonString);
		} else {
			String jsonString = redisUtil.get(key).toString();
			JavaType javaType = mapper.getTypeFactory()
					.constructParametricType(ArrayList.class, HeadLine.class);
			headLineList = mapper.readValue(jsonString, javaType);
		}
		return headLineList;
	}
	@Transactional
	public HeadLineExecution addHeadLine(HeadLine headLine,
										 CommonsMultipartFile thumbnail) throws IOException {
		if (headLine != null) {
			headLine.setCreateTime(new Date());
			headLine.setLastEditTime(new Date());
			if (thumbnail != null) {
				addThumbnail(headLine, thumbnail);
			}
			try {
				int effectedNum = headLineDao.insertHeadLine(headLine);
				if (effectedNum > 0) {
					findKeys();
					return new HeadLineExecution(HeadLineStateEnum.SUCCESS,
							headLine);
				} else {
					return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("添加区域信息失败:" + e.toString());
			}
		} else {
			return new HeadLineExecution(HeadLineStateEnum.EMPTY);
		}
	}

	@Transactional
	public HeadLineExecution modifyHeadLine(HeadLine headLine,
											CommonsMultipartFile thumbnail) throws IOException {
		if (headLine.getLineId() != null && headLine.getLineId() > 0) {
			headLine.setLastEditTime(new Date());
			if (thumbnail != null) {
				HeadLine tempHeadLine = headLineDao.queryHeadLineById(headLine
						.getLineId());
				if (tempHeadLine.getLineImg() != null) {
					PathUtil.deleteFile(tempHeadLine.getLineImg());
				}
				addThumbnail(headLine, thumbnail);
			}
			try {
				int effectedNum = headLineDao.updateHeadLine(headLine);
				if (effectedNum > 0) {
					findKeys();
					return new HeadLineExecution(HeadLineStateEnum.SUCCESS,
							headLine);
				} else {
					return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("更新头条信息失败:" + e.toString());
			}
		} else {
			return new HeadLineExecution(HeadLineStateEnum.EMPTY);
		}
	}


	@Transactional
	public HeadLineExecution removeHeadLine(long headLineId) {
		if (headLineId > 0) {
			try {
				HeadLine tempHeadLine = headLineDao
						.queryHeadLineById(headLineId);
				if (tempHeadLine.getLineImg() != null) {
					PathUtil.deleteFile(tempHeadLine.getLineImg());
				}
				int effectedNum = headLineDao.deleteHeadLine(headLineId);
				if (effectedNum > 0) {
					findKeys();
					return new HeadLineExecution(HeadLineStateEnum.SUCCESS);
				} else {
					return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("删除头条信息失败:" + e.toString());
			}
		} else {
			return new HeadLineExecution(HeadLineStateEnum.EMPTY);
		}
	}

	@Transactional
	public HeadLineExecution removeHeadLineList(List<Long> headLineIdList) {
		if (headLineIdList != null && headLineIdList.size() > 0) {
			try {
				List<HeadLine> headLineList = headLineDao
						.queryHeadLineByIds(headLineIdList);
				for (HeadLine headLine : headLineList) {
					if (headLine.getLineImg() != null) {
						PathUtil.deleteFile(headLine.getLineImg());
					}
				}
				int effectedNum = headLineDao
						.batchDeleteHeadLine(headLineIdList);
				if (effectedNum > 0) {
					findKeys();
					return new HeadLineExecution(HeadLineStateEnum.SUCCESS);
				} else {
					return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("删除头条信息失败:" + e.toString());
			}
		} else {
			return new HeadLineExecution(HeadLineStateEnum.EMPTY);
		}
	}

	/**
	 * 查找redis中key对应value
	 */
	private void findKeys() {
		String prefix = HLLISTKEY;
		Set<Object> keySet = redisUtil.sGet(prefix + "*");
		for (Object key : keySet) {
			redisUtil.del(key.toString());
		}
	}
	private void addThumbnail(HeadLine headLine, CommonsMultipartFile thumbnail) throws IOException {
		String dest = PathUtil.getHeadLineImagePath();
		ImageHolder imageHolder = new ImageHolder(thumbnail.getOriginalFilename(),thumbnail.getInputStream());
		String thumbnailAddr = ImageUtil.generateNormalImg(imageHolder, dest);
		headLine.setLineImg(thumbnailAddr);
	}
}
