package priv.hsy.redenvelops.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.service.RedisService;
import priv.hsy.redenvelops.utils.GetMoneyUtil;

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
        String redInfoMoney = rid + "redInfoMoney";

        redisTemplate.opsForValue().set(redInfoCount, count);
        redisTemplate.opsForValue().set(redInfoMoney, totalMoney);

        while (count > 0) {
            double result = GetMoneyUtil.getRandomMoney(count, totalMoney);
            redisTemplate.opsForList().rightPush(listkey, result);
            totalMoney -= result;
            count--;
        }
        log.info("redmoneylist = {}", listkey);
        return null;
    }


}
