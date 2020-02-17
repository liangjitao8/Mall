package org.cn.o2o.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;
import org.cn.o2o.model.LocalAuth;

public interface LocalAuthDao {

	LocalAuth queryLocalByUserNameAndPwd(@Param("userName") String userName,
										 @Param("password") String password);

	LocalAuth queryLocalByUserId(@Param("userId") long userId);

	int insertLocalAuth(LocalAuth localAuth);

	int updateLocalAuth(@Param("userId") Long userId,
                        @Param("userName") String userName,
                        @Param("password") String password,
                        @Param("newPassword") String newPassword,
                        @Param("lastEditTime") Date lastEditTime);
}
