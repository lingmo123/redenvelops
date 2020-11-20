package priv.hsy.redenvelops.service;

import priv.hsy.redenvelops.entity.Result;

public interface RedisService {
    String redRedisIndex(int rid, int count, double totalmoney);

    Result<Object> redRedisGetNoUid(String key, String redInfoCount, String redInfoMoney);

    Result<Object> redRedisGetUid(String key, String redInfoCount, String redInfoMoney, Integer uid);

}
