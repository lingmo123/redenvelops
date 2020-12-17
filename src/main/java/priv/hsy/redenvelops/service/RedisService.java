package priv.hsy.redenvelops.service;

import priv.hsy.redenvelops.entity.Result;

import java.math.BigInteger;

public interface RedisService {
    /**
     * 将红包金额以list形式存入redis
     *
     * @param count      红包数量
     * @param totalMoney 红包金额
     */
    String redRedisIndex(BigInteger rid, int count, String totalMoney);

    Result<Object> redGet(BigInteger rid , BigInteger uid);

    Result<Object> redRedisGetNoUid(String key, String redInfoCount, String redInfoMoney);

}
