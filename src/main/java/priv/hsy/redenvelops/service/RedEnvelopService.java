package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import priv.hsy.redenvelops.entity.RedEnvelop;

public interface RedEnvelopService {

    int insert(RedEnvelop redEnvelop);

    boolean updateById(RedEnvelop redEnvelop);

    RedEnvelop selectById(Integer id);

    RedEnvelop selectOne(Wrapper<RedEnvelop> wrapper);

    String updateenvelop(RedEnvelop redEnvelop, double remainMoney, int remainSize);
}
