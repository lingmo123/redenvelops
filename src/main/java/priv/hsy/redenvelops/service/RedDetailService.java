package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import priv.hsy.redenvelops.entity.RedDetail;
import priv.hsy.redenvelops.entity.Result;

import java.math.BigInteger;

public interface RedDetailService {
    RedDetail selectOne(Wrapper<RedDetail> wrapper);

    String insert(BigInteger uid, BigInteger rid, double money);

    Result<Object> selectDetails(BigInteger rid);
}
