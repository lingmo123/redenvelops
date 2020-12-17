package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import priv.hsy.redenvelops.entity.*;

import java.math.BigInteger;


public interface RedEnvelopService {
    RedEnvelop selectById(BigInteger rid);
    /**
     * 更新正在抢的红包数据
     *
     * @param rid 红包id
     * @return
     */
    boolean update(BigInteger rid, Double money);
    /**
     * 更新编辑的红包数据至数据库
     *
     * @param redEnvelop 红包实体类
     * @param count      红包数量
     * @param totalMoney 红包金额
     * @return
     */
    String updateEnvelop(RedEnvelop redEnvelop, int count, String totalMoney);
    /**
     * 发送红包
     *
     * @param rid 红包id
     * @return
     */
    Result<Object> sendRed(BigInteger rid);
    /**
     * 分页查询
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return
     */
    Result<Object> selectPage(int currentPage, int pageSize, QueryWrapper<RedEnvelop> wrapper);
    /**
     * 创建红包并插入到数据库
     *
     * @param redEnvelop 红包实体类
     * @return
     */
    Result<Object> setRed(RedEnvelop redEnvelop);
    /**
     * 编辑红包并更新数据库
     *
     * @param redEnvelop 红包实体类
     * @return
     */
    Result<Object> updateRed(RedEnvelop redEnvelop);
    /**
     * 红包抢完后更改红包状态为抢完
     *
     * @param rid 红包id
     * @return
     */
    String overRed(BigInteger rid);

    /**
     * 返回红包总详情
     * @return
     */
    Result<Object> selectAllRed();

    String test();
}
