package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import priv.hsy.redenvelops.entity.RedEnvelop;

public interface RedEnvelopService {

    String insert(RedEnvelop redEnvelop);

    boolean updateById(RedEnvelop redEnvelop);

    RedEnvelop selectById(Integer id);

    RedEnvelop selectOne(Wrapper<RedEnvelop> wrapper);

    String updateEnvelop(RedEnvelop redEnvelop, double remainMoney, int remainSize);

    String redmoneyinit(int rid, int count, double totalmoney);
}
