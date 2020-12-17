package priv.hsy.redenvelops.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.hsy.redenvelops.entity.Result;
import priv.hsy.redenvelops.enums.ResultEnum;
import priv.hsy.redenvelops.service.RedDetailService;
import priv.hsy.redenvelops.service.RedEnvelopService;
import priv.hsy.redenvelops.service.RedisService;
import priv.hsy.redenvelops.service.UserService;
import priv.hsy.redenvelops.utils.ArithmeticUtils;
import priv.hsy.redenvelops.utils.GetMoneyUtil;
import priv.hsy.redenvelops.utils.ResultUtil;

import java.math.BigInteger;
import java.util.concurrent.TimeUnit;

/**
*
* @author hsy
* @date 2020/12/8 9:33
*/

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private RedEnvelopService redEnvelopService;
    @Autowired
    private RedDetailService redDetailService;

    @Transactional( rollbackFor = Exception.class)
    @Override
    public String redRedisIndex(BigInteger rid, int count, String totalMoney) {
        String redMoneyList = rid + ":redMoneyList:";
        while (count > 0) {
            double result = GetMoneyUtil.getRandomMoney(count, Double.parseDouble(totalMoney));
            redisTemplate.opsForList().rightPush(redMoneyList, result);
            totalMoney = ArithmeticUtils.sub(totalMoney, String.valueOf(result)).toString();
            count--;
        }
        redisTemplate.expire(redMoneyList, 1, TimeUnit.DAYS);
        log.info("redMoneyList = {}", redMoneyList);
        return null;
    }

    @Override
    @Transactional( rollbackFor = Exception.class)
    public Result<Object> redGet(BigInteger rid, BigInteger uid) {
        String redMoneyList = rid + ":redMoneyList:";
        String userIdGet = rid + ":get" + uid;
        String redStatus = rid + ":redStatus:";
        String redInfo = rid + ":";
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "first", 5, TimeUnit.SECONDS);
        while (Boolean.FALSE.equals(lock)) {
            lock = redisTemplate.opsForValue().setIfAbsent("lock", "first", 5, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(lock)) {
                break;
            }
        }
        try {

            if (Boolean.FALSE.equals(redisTemplate.hasKey(redStatus))) {
                if (Boolean.FALSE.equals(redisTemplate.hasKey(userIdGet))) {
                    if (Boolean.TRUE.equals(redisTemplate.hasKey(redMoneyList))) {
                        if (redisTemplate.opsForList().size(redMoneyList) == 1) {
                            redEnvelopService.overRed(rid);
                            redisTemplate.opsForValue().set(redStatus, "红包已抢完！");
                        }
                        Double money = (Double) redisTemplate.boundListOps(redMoneyList).rightPop();
                        redisTemplate.opsForValue().setIfAbsent(userIdGet, money, 24, TimeUnit.HOURS);
                        //红包明细
                        redDetailService.insert(uid, rid, money);
                        //更新红包剩余数量
                        redEnvelopService.update(rid, money);
                        //用户抢到红包更新账户余额
                        userService.updateUserinfo(uid, money);
                        return ResultUtil.result(ResultEnum.RED_GET_SUCCESS,money);
                    } else {
                        return ResultUtil.result(ResultEnum.RED_COUNT_NO);
                    }
                } else {
                    return ResultUtil.result(ResultEnum.USE_RGET_NO);
                }
            } else {
                return ResultUtil.result(ResultEnum.RED_COUNT_NO);
            }
        } catch (Exception e) {
            return ResultUtil.result(ResultEnum.FAIL );
        } finally {
            redisTemplate.delete("lock");
        }

    }

    @Override
    @Transactional( rollbackFor = Exception.class)
    public Result<Object> redRedisGetNoUid(String key, String redInfoCount, String redInfoMoney) {
        return ResultUtil.result(ResultEnum.SUCCESS);
    }

}
