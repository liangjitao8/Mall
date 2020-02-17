package org.cn.o2o.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.cn.o2o.dao.AreaDao;
import org.cn.o2o.dto.AreaExecution;
import org.cn.o2o.enums.AreaStateEnum;
import org.cn.o2o.exception.AreaOperationException;
import org.cn.o2o.model.Area;
import org.cn.o2o.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AreaService{
	private static String AREALISTKEY = "arealist";
	@Autowired
	private AreaDao areaDao;
	@Autowired
	private RedisUtil redisUtil;

	@Transactional
	public List<Area> getAreaList(){
		String key = AREALISTKEY;
		List<Area> areaList = null;
		ObjectMapper mapper = new ObjectMapper();
		if(!redisUtil.hasKey(key)) {
			areaList=areaDao.queryArea();
			String jsonString;
			try {
				jsonString = mapper.writeValueAsString(areaList);
			} catch (JsonProcessingException e) {
				throw new AreaOperationException(e.getMessage());
			}
			redisUtil.set(key,jsonString);
		}else {
			String jsonString = redisUtil.get(key).toString();
			JavaType javaType =
					mapper.getTypeFactory().constructParametricType(ArrayList.class, Area.class);
			try {
				areaList = mapper.readValue(jsonString, javaType);
			} catch (JsonParseException e) {
				throw new AreaOperationException(e.getMessage());
			}catch (JsonMappingException e) {
				throw new AreaOperationException(e.getMessage());
			}catch (IOException e) {
				throw new AreaOperationException(e.getMessage());
			}
		}
		return areaDao.queryArea();
	}
	@Transactional
	public AreaExecution addArea(Area area) {
		if (area.getAreaName() != null && !"".equals(area.getAreaName())) {
			area.setCreateTime(new Date());
			area.setLastEditTime(new Date());
			try {
				int effectedNum = areaDao.insertArea(area);
				if (effectedNum > 0) {
					String key = AREALISTKEY;
					if (redisUtil.hasKey(key)) {
						redisUtil.del(key);
					}
					return new AreaExecution(AreaStateEnum.SUCCESS, area);
				} else {
					return new AreaExecution(AreaStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("添加区域信息失败:" + e.toString());
			}
		} else {
			return new AreaExecution(AreaStateEnum.EMPTY);
		}
	}

	@Transactional
	public AreaExecution modifyArea(Area area) {
		if (area.getAreaId() != null && area.getAreaId() > 0) {
			area.setLastEditTime(new Date());
			try {
				int effectedNum = areaDao.updateArea(area);
				if (effectedNum > 0) {
					String key = AREALISTKEY;
					if (redisUtil.hasKey(key)) {
						redisUtil.del(key);
					}
					return new AreaExecution(AreaStateEnum.SUCCESS, area);
				} else {
					return new AreaExecution(AreaStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("更新区域信息失败:" + e.toString());
			}
		} else {
			return new AreaExecution(AreaStateEnum.EMPTY);
		}
	}

	@Transactional
	public AreaExecution removeArea(long areaId) {
		if (areaId > 0) {
			try {
				int effectedNum = areaDao.deleteArea(areaId);
				if (effectedNum > 0) {
					String key = AREALISTKEY;
					if (redisUtil.hasKey(key)) {
						redisUtil.del(key);
					}
					return new AreaExecution(AreaStateEnum.SUCCESS);
				} else {
					return new AreaExecution(AreaStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("删除区域信息失败:" + e.toString());
			}
		} else {
			return new AreaExecution(AreaStateEnum.EMPTY);
		}
	}

	@Transactional
	public AreaExecution removeAreaList(List<Long> areaIdList) {
		if (areaIdList != null && areaIdList.size() > 0) {
			try {
				int effectedNum = areaDao.batchDeleteArea(areaIdList);
				if (effectedNum > 0) {
					String key = AREALISTKEY;
					if (redisUtil.hasKey(key)) {
						redisUtil.del(key);
					}
					return new AreaExecution(AreaStateEnum.SUCCESS);
				} else {
					return new AreaExecution(AreaStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("删除区域信息失败:" + e.toString());
			}
		} else {
			return new AreaExecution(AreaStateEnum.EMPTY);
		}
	}
}
