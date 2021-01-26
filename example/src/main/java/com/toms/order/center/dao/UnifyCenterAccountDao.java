package com.toms.order.center.dao;

import com.toms.order.center.entity.UnifyCenterAccount;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 统一用户中心表(UnifyCenterAccount)表数据库访问层
 *
 * @author chenxiaotong
 * @since 2020-12-15 19:44:08
 */
public interface UnifyCenterAccountDao {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    UnifyCenterAccount queryById(Integer id);

    /**
     * 查询指定行数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<UnifyCenterAccount> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);


    /**
     * 通过实体作为筛选条件查询
     *
     * @param unifyCenterAccount 实例对象
     * @return 对象列表
     */
    List<UnifyCenterAccount> queryAll(UnifyCenterAccount unifyCenterAccount);

    /**
     * 新增数据
     *
     * @param unifyCenterAccount 实例对象
     * @return 影响行数
     */
    int insert(UnifyCenterAccount unifyCenterAccount);

    /**
     * 修改数据
     *
     * @param unifyCenterAccount 实例对象
     * @return 影响行数
     */
    int update(UnifyCenterAccount unifyCenterAccount);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 影响行数
     */
    int deleteById(Integer id);

}