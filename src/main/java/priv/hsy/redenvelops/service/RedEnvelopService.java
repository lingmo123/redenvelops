package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import priv.hsy.redenvelops.entity.*;

import java.math.BigInteger;


public interface RedEnvelopService {
    RedEnvelop selectById(BigInteger rid);

    boolean update(BigInteger rid, Double money);

    String updateEnvelop(RedEnvelop redEnvelop, int count, String totalMoney);

    Result<Object> sendRed(BigInteger rid);

    Result<Object> selectPage(int currentPage, int pageSize, QueryWrapper<RedEnvelop> wrapper);

    Result<Object> setRed(RedEnvelop redEnvelop);

    Result<Object> updateRed(RedEnvelop redEnvelop);

    String overRed(BigInteger rid);
}
