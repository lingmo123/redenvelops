package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import priv.hsy.redenvelops.entity.RedDetail;
import priv.hsy.redenvelops.entity.RedEnvelop;

public interface RedDetailService {
    RedDetail selectOne(Wrapper<RedDetail> wrapper);

    int insert(RedDetail redDetail);

    String updateRedDetail(RedEnvelop redEnvelop, double money, int rid, int id);
}
