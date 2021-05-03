package com.felix.middleware.model.mapper;

import com.felix.middleware.model.entity.UserAccount;
import org.apache.ibatis.annotations.Param;

public interface UserAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(UserAccount record);

    int insertSelective(UserAccount record);

    UserAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(UserAccount record);

    int updateByPrimaryKey(UserAccount record);

    UserAccount selectByUserId(@Param("userId") Integer userId);

    /**
     * 更新账户金额
     * @param amount
     * @param id
     * @return
     */
    int updateAmount(@Param("amount") Double amount, @Param("id") Integer id);

    /**
     * 根据主键id和version更新金额
     * @param money
     * @param id
     * @param version
     * @return
     */
    int updateByPKVersion(@Param("amount") Double money, @Param("id") Integer id, @Param("version") Integer version);

    /**
     * 根据用户id查询记录-for update方式-悲观锁
     * @param userId
     * @return
     */
    UserAccount selectByUserIdLock(@Param("userId") Integer userId);

    /**
     * 更新账户金额-悲观锁
     * @param amount
     * @param id
     * @return
     */
    int updateAmountLock(@Param("amount") Double amount, @Param("id") Integer id);

}