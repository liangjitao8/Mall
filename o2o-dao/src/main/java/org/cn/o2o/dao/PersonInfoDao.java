package org.cn.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.cn.o2o.model.PersonInfo;


public interface PersonInfoDao {

	/**
	 * 
	 * @param personInfoCondition
	 * @param rowIndex
	 * @param pageSize
	 * @return
	 */
	List<PersonInfo> queryPersonInfoList(
            @Param("personInfoCondition") PersonInfo personInfoCondition,
            @Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

	/**
	 * 
	 * @param personInfoCondition
	 * @return
	 */
	int queryPersonInfoCount(
            @Param("personInfoCondition") PersonInfo personInfoCondition);

	PersonInfo queryPersonInfoById(long userId);

	int insertPersonInfo(PersonInfo personInfo);


	int updatePersonInfo(PersonInfo personInfo);

	int deletePersonInfo(long userId);

}
