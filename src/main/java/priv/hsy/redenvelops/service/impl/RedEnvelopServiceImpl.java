package priv.hsy.redenvelops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.RedEnvelop;
import priv.hsy.redenvelops.mapper.RedEnvelopMapper;
import priv.hsy.redenvelops.service.RedEnvelopService;
import priv.hsy.redenvelops.service.RedisService;
import priv.hsy.redenvelops.utils.GetMoneyUtil;

import java.sql.Timestamp;
import java.util.List;

@Service
@Slf4j
public class RedEnvelopServiceImpl extends ServiceImpl<RedEnvelopMapper, RedEnvelop> implements RedEnvelopService {

    @Autowired
    private RedisService redisService;

    @Override
    public List<RedEnvelop> selectAll() {

        return this.baseMapper.selectList(null);
    }

    /**
     * 发送红包的红包详情存入数据库，并设置每个红包金额
     *
     * @param redEnvelop 实体类
     * @return
     */
    @Override
    public String insert(RedEnvelop redEnvelop) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        redEnvelop.setSendTime(time);
        this.baseMapper.insert(redEnvelop);
        try {
            redisService.redRedisIndex(redEnvelop.getRid(), redEnvelop.getCount(), redEnvelop.getTotalMoney());
        } catch (Exception e) {
            return "redis设置红包列表出错！";
        }
        return "success！";
    }

    /**
     * 更新正在抢的红包数据
     *
     * @param redEnvelop 实体类
     * @return
     */
    @Override
    public boolean updateById(RedEnvelop redEnvelop) {
        this.baseMapper.updateById(redEnvelop);
        return true;
    }

    /**
     * 数据库查询在抢红包
     *
     * @param wrapper 条件语句
     * @return
     */
    @Override
    public RedEnvelop selectOne(Wrapper<RedEnvelop> wrapper) {

        return this.baseMapper.selectOne(wrapper);
    }


    /**
     * 更新红包数据
     *
     * @param redEnvelop  实体类
     * @param remainMoney 红包剩余金额
     * @param remainSize  红包剩余数量
     * @return
     */
    @Override
    public String updateEnvelop(RedEnvelop redEnvelop, double remainMoney, int remainSize) {
        redEnvelop.setRestMoney(remainMoney);
        redEnvelop.setRestCount(remainSize);
        updateById(redEnvelop);
        return "更新成功";
    }

}
