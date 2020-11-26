package priv.hsy.redenvelops.service;

import priv.hsy.redenvelops.entity.Result;

import java.math.BigInteger;

public interface RedisService {
    String redRedisIndex(BigInteger rid, int count, String totalmoney);

    Result<Object> redGet(BigInteger rid , BigInteger uid);

    Result<Object> redRedisGetNoUid(String key, String redInfoCount, String redInfoMoney);

}
