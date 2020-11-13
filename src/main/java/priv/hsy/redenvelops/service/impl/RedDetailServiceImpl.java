package priv.hsy.redenvelops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.RedDetail;
import priv.hsy.redenvelops.entity.RedEnvelop;
import priv.hsy.redenvelops.mapper.RedDtailMapper;
import priv.hsy.redenvelops.service.RedDetailService;


@Service
public class RedDetailServiceImpl extends ServiceImpl<RedDtailMapper, RedDetail> implements RedDetailService {

    @Override
    public RedDetail selectOne(Wrapper<RedDetail> wrapper) {

        return this.baseMapper.selectOne(wrapper);
    }

    @Override
    public int insert(RedDetail redDetail) {
        this.baseMapper.insert(redDetail);
        return 1;
    }

    @Override
    public String updateRedDetail(RedEnvelop redEnvelop, double money, int rid, int id) {
        RedDetail redDetail = new RedDetail();
        redDetail.setRid(redEnvelop.getRid());
        redDetail.setGetmoney(money);
        redDetail.setReceiveid(id);
        insert(redDetail);
        return "更新红包明细成功";
    }
}
