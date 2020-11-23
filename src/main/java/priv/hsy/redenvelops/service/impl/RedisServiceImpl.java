package priv.hsy.redenvelops.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.Result;
import priv.hsy.redenvelops.enums.ResultEnum;
import priv.hsy.redenvelops.service.RedisService;
import priv.hsy.redenvelops.utils.GetMoneyUtil;
import priv.hsy.redenvelops.utils.ResultUtil;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 将红包金额以list形式存入redis
     *
     * @param count      红包数量
     * @param totalMoney 红包金额
     */
    @Override
    public String redRedisIndex(int rid, int count, double totalMoney) {
        String listkey = rid + "redMoneylist";
        String redInfoCount = rid + "redInfoCount";

        redisTemplate.opsForValue().set(redInfoCount, count);

        while (count > 0) {
            double result = GetMoneyUtil.getRandomMoney(count, totalMoney);
            redisTemplate.opsForList().rightPush(listkey, result);
            totalMoney -= result;
            count--;
        }
        log.info("redmoneylist = {}", listkey);
        return null;
    }

    @Override
    public Result<Object> redRedisGetNoUid(String key, String redInfoCount, String redInfoMoney) {
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "first", 2, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(lock)) {
            return ResultUtil.result(ResultEnum.FAIL, "lock");
        }
        try {
            Double restMoney = (double) redisTemplate.opsForValue().get(redInfoMoney);
            Integer restSize = (int) redisTemplate.opsForValue().get(redInfoCount);

            log.info("restMoney = {}", restMoney);
            log.info("restSize = {}", restSize);
            if (restSize != null && restSize > 0) {
                redisTemplate.opsForValue().decrement(redInfoCount);
                Double money = (Double) redisTemplate.boundListOps(key).rightPop();
                if (money == null) {
                    return ResultUtil.result(ResultEnum.FAIL, "monry == null");
                }
                restMoney -= money;
                redisTemplate.opsForValue().set(redInfoMoney, restMoney);
                return ResultUtil.result(ResultEnum.REDGET_SUCCESS);
            } else {
                return ResultUtil.result(ResultEnum.REDCOUNT_NO);
            }
        } catch (Exception e) {
            return ResultUtil.result(ResultEnum.FAIL, "catch");
        } finally {
            redisTemplate.delete("lock");
        }
    }


}
