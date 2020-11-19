package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import priv.hsy.redenvelops.entity.RedEnvelop;

import java.util.List;

public interface RedEnvelopService {

    RedEnvelop selectOne(Wrapper<RedEnvelop> wrapper);

    List<RedEnvelop> selectAll();

    String insert(RedEnvelop redEnvelop);

    boolean updateById(RedEnvelop redEnvelop);

    String updateEnvelop(RedEnvelop redEnvelop, double remainMoney, int remainSize);

}
