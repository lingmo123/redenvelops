package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import priv.hsy.redenvelops.entity.RedDetail;
import priv.hsy.redenvelops.entity.RedEnvelop;

import java.math.BigInteger;
import java.util.List;

public interface RedDetailService {
    RedDetail selectOne(Wrapper<RedDetail> wrapper);

    String insert(BigInteger uid, BigInteger rid, double money);

    List<RedDetail> selectDetails(BigInteger rid);
}
