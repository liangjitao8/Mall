package org.cn.o2o.service;

import java.util.List;

import org.cn.o2o.dao.PersonInfoDao;
import org.cn.o2o.dto.PersonInfoExecution;
import org.cn.o2o.enums.PersonInfoStateEnum;
import org.cn.o2o.model.PersonInfo;
import org.cn.o2o.utils.PageCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class PersonInfoService {
	@Autowired
	private PersonInfoDao personInfoDao;


	public PersonInfo getPersonInfoById(Long userId) {
		return personInfoDao.queryPersonInfoById(userId);
	}


	public PersonInfoExecution getPersonInfoList(
			PersonInfo personInfoCondition, int pageIndex, int pageSize) {
		int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		List<PersonInfo> personInfoList = personInfoDao.queryPersonInfoList(
				personInfoCondition, rowIndex, pageSize);
		int count = personInfoDao.queryPersonInfoCount(personInfoCondition);
		PersonInfoExecution se = new PersonInfoExecution();
		if (personInfoList != null) {
			se.setPersonInfoList(personInfoList);
			se.setCount(count);
		} else {
			se.setState(PersonInfoStateEnum.INNER_ERROR.getState());
		}
		return se;
	}

	@Transactional
	public PersonInfoExecution addPersonInfo(PersonInfo personInfo) {
		if (personInfo == null) {
			return new PersonInfoExecution(PersonInfoStateEnum.EMPTY);
		} else {
			try {
				int effectedNum = personInfoDao.insertPersonInfo(personInfo);
				if (effectedNum <= 0) {
					return new PersonInfoExecution(
							PersonInfoStateEnum.INNER_ERROR);
				} else {// 创建成功
					personInfo = personInfoDao.queryPersonInfoById(personInfo
							.getUserId());
					return new PersonInfoExecution(PersonInfoStateEnum.SUCCESS,
							personInfo);
				}
			} catch (Exception e) {
				throw new RuntimeException("addPersonInfo error: "
						+ e.getMessage());
			}
		}
	}

	@Transactional
	public PersonInfoExecution modifyPersonInfo(PersonInfo personInfo) {
		if (personInfo == null || personInfo.getUserId() == null) {
			return new PersonInfoExecution(PersonInfoStateEnum.EMPTY);
		} else {
			try {
				int effectedNum = personInfoDao.updatePersonInfo(personInfo);
				if (effectedNum <= 0) {
					return new PersonInfoExecution(
							PersonInfoStateEnum.INNER_ERROR);
				} else {// 创建成功
					personInfo = personInfoDao.queryPersonInfoById(personInfo
							.getUserId());
					return new PersonInfoExecution(PersonInfoStateEnum.SUCCESS,
							personInfo);
				}
			} catch (Exception e) {
				throw new RuntimeException("updatePersonInfo error: "
						+ e.getMessage());
			}
		}
	}

}
