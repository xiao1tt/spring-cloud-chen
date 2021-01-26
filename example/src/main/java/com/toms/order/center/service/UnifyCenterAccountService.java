package com.toms.order.center.service;

import com.toms.order.center.entity.UnifyCenterAccount;
import java.util.List;

/**
 * 统一用户中心表(UnifyCenterAccount)表服务接口
 *
 * @author chenxiaotong
 * @since 2020-12-15 19:44:09
 */
public interface UnifyCenterAccountService {

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    UnifyCenterAccount queryById(Integer id);

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    List<UnifyCenterAccount> queryAllByLimit(int offset, int limit);

    /**
     * 新增数据
     *
     * @param unifyCenterAccount 实例对象
     * @return 实例对象
     */
    UnifyCenterAccount insert(UnifyCenterAccount unifyCenterAccount);

    /**
     * 修改数据
     *
     * @param unifyCenterAccount 实例对象
     * @return 实例对象
     */
    UnifyCenterAccount update(UnifyCenterAccount unifyCenterAccount);

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    boolean deleteById(Integer id);

}