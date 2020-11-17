package priv.hsy.redenvelops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.RedEnvelop;
import priv.hsy.redenvelops.mapper.RedEnvelopMapper;
import priv.hsy.redenvelops.service.RedEnvelopService;
import priv.hsy.redenvelops.utils.GetMoneyUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RedEnvelopServiceImpl extends ServiceImpl<RedEnvelopMapper, RedEnvelop> implements RedEnvelopService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 发送红包的红包详情存入数据库，并设置每个红包金额
     * @param redEnvelop 实体类
     * @return
     */
    @Override
    public String insert(RedEnvelop redEnvelop) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        redEnvelop.setSendTime(time);
        this.baseMapper.insert(redEnvelop);
        try{
        redmoneyinit(redEnvelop.getRid(), redEnvelop.getCount(), redEnvelop.getTotalMoney());
        }
        catch (Exception e){
            return "redis设置红包列表出错！";
        }
        return "success！";
    }

    /**
     * 更新正在抢的红包数据
     * @param redEnvelop 实体类
     * @return
     */
    @Override
    public boolean updateById(RedEnvelop redEnvelop) {
        this.baseMapper.updateById(redEnvelop);
        return true;
    }

    /**
     * 查询确定的id的正在抢的红包详情
     * @param id 红包id
     * @return
     */
    @Override
    public RedEnvelop selectById(Integer id) {
        return this.baseMapper.selectById(id);
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
     * @param redEnvelop 实体类
     * @param remainMoney 红包剩余金额
     * @param remainSize 红包剩余数量
     * @return
     */
    @Override
    public String updateEnvelop(RedEnvelop redEnvelop, double remainMoney, int remainSize) {
        redEnvelop.setRestMoney(remainMoney);
        redEnvelop.setRestCount(remainSize);
        updateById(redEnvelop);
        return "更新成功";
    }

    /**
     * 将红包金额以list形式存入redis
     *
     * @param count      红包数量
     * @param totalmoney 红包金额
     */
    @Override
    public String redmoneyinit(int rid, int count, double totalmoney) {
        String key = rid + "redmoneylist";
        List<Double> redmoneylist = new ArrayList<>();
        while (count > 0) {
            double result = GetMoneyUtil.getRandomMoney(count, totalmoney);
            redmoneylist.add(result);
            redisTemplate.opsForList().rightPush(key, result);
            totalmoney -= result;
            count--;
        }
        log.info("redmoneylist = {}", key);
        return null;
    }
}
