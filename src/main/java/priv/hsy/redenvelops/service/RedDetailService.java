package priv.hsy.redenvelops.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import priv.hsy.redenvelops.entity.RedDetail;
import priv.hsy.redenvelops.entity.RedEnvelop;

import java.util.List;

public interface RedDetailService {
    RedDetail selectOne(Wrapper<RedDetail> wrapper);

//    String insert(int uid, int rid, double money);

    String insert(int uid, int rid, double money);

    List<RedDetail> selectDetails(int rid);
}
