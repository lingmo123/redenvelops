package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import priv.hsy.redenvelops.entity.RedEnvelop;
import priv.hsy.redenvelops.entity.RedEnvelopPageBean;
import priv.hsy.redenvelops.entity.RedInfoPageBean;

import java.util.List;

public interface RedEnvelopService {

    RedEnvelop selectOne(Wrapper<RedEnvelop> wrapper);

    List<RedEnvelop> selectAll();

    String insert(RedEnvelop redEnvelop);

    boolean update(Integer rid, Double money);

    String updateEnvelop(RedEnvelop redEnvelop, double remainMoney, int remainSize);

    RedEnvelopPageBean selectPage(int currentPage, int pageSize);

}
