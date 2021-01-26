package com.toms.order.center.service.impl;

import com.toms.order.center.entity.UnifyCenterAccount;
import com.toms.order.center.dao.UnifyCenterAccountDao;
import com.toms.order.center.service.UnifyCenterAccountService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 统一用户中心表(UnifyCenterAccount)表服务实现类
 *
 * @author chenxiaotong
 * @since 2020-12-15 19:44:09
 */
@Service("unifyCenterAccountService")
public class UnifyCenterAccountServiceImpl implements UnifyCenterAccountService {
    @Resource
    private UnifyCenterAccountDao unifyCenterAccountDao;

    /**
     * 通过ID查询单条数据
     *
     * @param id 主键
     * @return 实例对象
     */
    @Override
    public UnifyCenterAccount queryById(Integer id) {
        return this.unifyCenterAccountDao.queryById(id);
    }

    /**
     * 查询多条数据
     *
     * @param offset 查询起始位置
     * @param limit 查询条数
     * @return 对象列表
     */
    @Override
    public List<UnifyCenterAccount> queryAllByLimit(int offset, int limit) {
        return this.unifyCenterAccountDao.queryAllByLimit(offset, limit);
    }

    /**
     * 新增数据
     *
     * @param unifyCenterAccount 实例对象
     * @return 实例对象
     */
    @Override
    public UnifyCenterAccount insert(UnifyCenterAccount unifyCenterAccount) {
        this.unifyCenterAccountDao.insert(unifyCenterAccount);
        return unifyCenterAccount;
    }

    /**
     * 修改数据
     *
     * @param unifyCenterAccount 实例对象
     * @return 实例对象
     */
    @Override
    public UnifyCenterAccount update(UnifyCenterAccount unifyCenterAccount) {
        this.unifyCenterAccountDao.update(unifyCenterAccount);
        return this.queryById(unifyCenterAccount.getId());
    }

    /**
     * 通过主键删除数据
     *
     * @param id 主键
     * @return 是否成功
     */
    @Override
    public boolean deleteById(Integer id) {
        return this.unifyCenterAccountDao.deleteById(id) > 0;
    }
}