package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import priv.hsy.redenvelops.entity.*;

import java.math.BigInteger;


public interface RedEnvelopService {
    RedEnvelop selectById(BigInteger rid);

    boolean update(BigInteger rid, Double money);

    String updateEnvelop(RedEnvelop redEnvelop, int count, String totalMoney);

    String sendRed(BigInteger rid);

    RedEnvelopPageBean selectPage(int currentPage, int pageSize, QueryWrapper wrapper);

    Result<Object> setRed(RedEnvelop redEnvelop);

    String overRed(BigInteger rid);
}
