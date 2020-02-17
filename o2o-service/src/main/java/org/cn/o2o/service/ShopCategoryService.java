package org.cn.o2o.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.cn.o2o.dao.ShopCategoryDao;
import org.cn.o2o.dto.ImageHolder;
import org.cn.o2o.dto.ShopCategoryExecution;
import org.cn.o2o.enums.ShopCategoryStateEnum;
import org.cn.o2o.exception.ShopOperationException;
import org.cn.o2o.model.ShopCategory;
import org.cn.o2o.utils.FileUtil;
import org.cn.o2o.utils.ImageUtil;
import org.cn.o2o.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Service
public class ShopCategoryService{
	private static String SCLISTKEY = "shopcategorylist";
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private ShopCategoryDao shopCategoryDao;

	public List<ShopCategory> getFirstLevelShopCategoryList()
			throws IOException {
		String key = SCLISTKEY;
		List<ShopCategory> shopCategoryList = null;
		ObjectMapper mapper = new ObjectMapper();
		if (!redisUtil.hasKey(key)) {
			ShopCategory shopCategoryCondition = new ShopCategory();
			// 当shopCategoryId不为空的时候，查询的条件会变为 where parent_id is null
			shopCategoryCondition.setShopCategoryId(-1L);
			shopCategoryList = shopCategoryDao
					.queryShopCategory(shopCategoryCondition);
			String jsonString = mapper.writeValueAsString(shopCategoryList);
			redisUtil.set(key, jsonString);
		} else {
			String jsonString = redisUtil.get(key).toString();
			JavaType javaType = mapper.getTypeFactory()
					.constructParametricType(ArrayList.class,
							ShopCategory.class);
			shopCategoryList = mapper.readValue(jsonString, javaType);
		}
		return shopCategoryList;
	}

	public List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition) {
		String key = SCLISTKEY;
		List<ShopCategory> shopCategoryList = null;
		ObjectMapper mapper = new ObjectMapper();
		if(shopCategoryCondition==null) {
			key = key +"_allfirstlevel";
		}else if(shopCategoryCondition!=null&&shopCategoryCondition.getParent()!=null
				&&shopCategoryCondition.getParent().getShopCategoryId()!=null) {
			key = key +"_parent"+shopCategoryCondition.getParent().getShopCategoryId();
		}else if(shopCategoryCondition!=null) {
			key = key +"_allsecondlevel";
		}
		if (!redisUtil.hasKey(key)) {
			shopCategoryList = shopCategoryDao
					.queryShopCategory(shopCategoryCondition);
			String jsonString;
			try {
				jsonString = mapper.writeValueAsString(shopCategoryList);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new ShopOperationException(e.getMessage());
			}
			redisUtil.set(key,jsonString);
		} else {
			String jsonString = redisUtil.get(key).toString();
			JavaType javaType = mapper.getTypeFactory()
					.constructParametricType(ArrayList.class,
							ShopCategory.class);
			try {
				shopCategoryList = mapper.readValue(jsonString, javaType);
			}catch (JsonParseException e) {
				e.printStackTrace();
				throw new ShopOperationException(e.getMessage());
			}catch (JsonMappingException e) {
				e.printStackTrace();
				throw new ShopOperationException(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				throw new ShopOperationException(e.getMessage());
			}
		}
		return shopCategoryList;
	}

	@Transactional
	public ShopCategoryExecution addShopCategory(ShopCategory shopCategory,
												 CommonsMultipartFile thumbnail) throws IOException {
		if (shopCategory != null) {
			shopCategory.setCreateTime(new Date());
			shopCategory.setLastEditTime(new Date());
			if (thumbnail != null) {
				addThumbnail(shopCategory, thumbnail);
			}
			try {
				int effectedNum = shopCategoryDao
						.insertShopCategory(shopCategory);
				if (effectedNum > 0) {
					String prefix = SCLISTKEY;
					Set<String> keySet = redisUtil.keys(prefix + "*");
					for (String key : keySet) {
						redisUtil.del(key);
					}
					return new ShopCategoryExecution(
							ShopCategoryStateEnum.SUCCESS, shopCategory);
				} else {
					return new ShopCategoryExecution(
							ShopCategoryStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("添加店铺类别信息失败:" + e.toString());
			}
		} else {
			return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
		}
	}
	public ShopCategory getShopCategoryById(Long shopCategoryId) {
		List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
		try {
			shopCategoryList = getFirstLevelShopCategoryList();
			shopCategoryList.addAll(getAllSecondLevelShopCategory());
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (ShopCategory sc : shopCategoryList) {
			if (shopCategoryId == sc.getShopCategoryId()) {
				return sc;
			}
		}
		ShopCategory sc = shopCategoryDao.queryShopCategoryById(shopCategoryId);
		if (sc != null) {
			return sc;
		} else {
			return null;
		}

	}
	public List<ShopCategory> getAllSecondLevelShopCategory()
			throws IOException {
		String key = SCLISTKEY + "ALLSECOND";
		List<ShopCategory> shopCategoryList = null;
		ObjectMapper mapper = new ObjectMapper();
		if (!redisUtil.hasKey(key)) {
			ShopCategory shopCategoryCondition = new ShopCategory();
			// 当shopCategoryDesc不为空的时候，查询的条件会变为 where parent_id is not null
			shopCategoryCondition.setShopCategoryDesc("ALLSECOND");
			shopCategoryList = shopCategoryDao
					.queryShopCategory(shopCategoryCondition);
			String jsonString = mapper.writeValueAsString(shopCategoryList);
			redisUtil.set(key, jsonString);
		} else {
			String jsonString = redisUtil.get(key).toString();
			JavaType javaType = mapper.getTypeFactory()
					.constructParametricType(ArrayList.class,
							ShopCategory.class);
			shopCategoryList = mapper.readValue(jsonString, javaType);
		}
		return shopCategoryList;
	}
	@Transactional
	public ShopCategoryExecution modifyShopCategory(ShopCategory shopCategory,
													CommonsMultipartFile thumbnail, boolean thumbnailChange) throws IOException {
		if (shopCategory.getShopCategoryId() != null
				&& shopCategory.getShopCategoryId() > 0) {
			shopCategory.setLastEditTime(new Date());
			if (thumbnail != null && thumbnailChange == true) {
				ShopCategory tempShopCategory = shopCategoryDao
						.queryShopCategoryById(shopCategory.getShopCategoryId());
				if (tempShopCategory.getShopCategoryImg() != null) {
					FileUtil.deleteFile(tempShopCategory.getShopCategoryImg());
				}
				addThumbnail(shopCategory, thumbnail);
			}
			try {
				int effectedNum = shopCategoryDao
						.updateShopCategory(shopCategory);
				if (effectedNum > 0) {
					String prefix = SCLISTKEY;
					Set<String> keySet = redisUtil.keys(prefix + "*");
					for (String key : keySet) {
						redisUtil.del(key);
					}
					return new ShopCategoryExecution(
							ShopCategoryStateEnum.SUCCESS, shopCategory);
				} else {
					return new ShopCategoryExecution(
							ShopCategoryStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("更新店铺类别信息失败:" + e.toString());
			}
		} else {
			return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
		}
	}

	@Transactional
	public ShopCategoryExecution removeShopCategory(long shopCategoryId) {
		if (shopCategoryId > 0) {
			try {
				ShopCategory tempShopCategory = shopCategoryDao
						.queryShopCategoryById(shopCategoryId);
				if (tempShopCategory.getShopCategoryImg() != null) {
					FileUtil.deleteFile(tempShopCategory.getShopCategoryImg());
				}
				int effectedNum = shopCategoryDao
						.deleteShopCategory(shopCategoryId);
				if (effectedNum > 0) {
					String prefix = SCLISTKEY;
					Set<String> keySet = redisUtil.keys(prefix + "*");
					for (String key : keySet) {
						redisUtil.del(key);
					}
					return new ShopCategoryExecution(
							ShopCategoryStateEnum.SUCCESS);
				} else {
					return new ShopCategoryExecution(
							ShopCategoryStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("删除店铺类别信息失败:" + e.toString());
			}
		} else {
			return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
		}
	}

	@Transactional
	public ShopCategoryExecution removeShopCategoryList(
			List<Long> shopCategoryIdList) {
		if (shopCategoryIdList != null && shopCategoryIdList.size() > 0) {
			try {
				List<ShopCategory> shopCategoryList = shopCategoryDao
						.queryShopCategoryByIds(shopCategoryIdList);
				for (ShopCategory shopCategory : shopCategoryList) {
					if (shopCategory.getShopCategoryImg() != null) {
						FileUtil.deleteFile(shopCategory.getShopCategoryImg());
					}
				}
				int effectedNum = shopCategoryDao
						.batchDeleteShopCategory(shopCategoryIdList);
				if (effectedNum > 0) {
					String prefix = SCLISTKEY;
					Set<String> keySet = redisUtil.keys(prefix + "*");
					for (String key : keySet) {
						redisUtil.del(key);
					}
					return new ShopCategoryExecution(
							ShopCategoryStateEnum.SUCCESS);
				} else {
					return new ShopCategoryExecution(
							ShopCategoryStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("删除店铺类别信息失败:" + e.toString());
			}
		} else {
			return new ShopCategoryExecution(ShopCategoryStateEnum.EMPTY);
		}
	}
	private void addThumbnail(ShopCategory shopCategory,
							  CommonsMultipartFile thumbnail) throws IOException {
		String dest = FileUtil.getShopCategoryImagePath();
		ImageHolder imageHolder=new ImageHolder(thumbnail.getOriginalFilename(),thumbnail.getInputStream());
		String thumbnailAddr = ImageUtil.generateNormalImg(imageHolder, dest);
		shopCategory.setShopCategoryImg(thumbnailAddr);
	}
}
