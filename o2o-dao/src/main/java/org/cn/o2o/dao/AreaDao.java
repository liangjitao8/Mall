package org.cn.o2o.dao;

import org.cn.o2o.model.Area;

import java.util.List;

public interface AreaDao {
	/**
	 * 列出区域列表
	 * @return areaList
	 */
	List<Area> queryArea();
	/**
	 *
	 * @param area
	 * @return
	 */
	int insertArea(Area area);

	/**
	 *
	 * @param area
	 * @return
	 */
	int updateArea(Area area);

	/**
	 *
	 * @param areaId
	 * @return
	 */
	int deleteArea(long areaId);

	/**
	 *
	 * @param areaIdList
	 * @return
	 */
	int batchDeleteArea(List<Long> areaIdList);
}
