package priv.hsy.redenvelops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.RedEnvelop;
import priv.hsy.redenvelops.mapper.RedEnvelopMapper;
import priv.hsy.redenvelops.service.RedEnvelopService;

@Service
public class RedEnvelopServiceImpl extends ServiceImpl<RedEnvelopMapper, RedEnvelop> implements RedEnvelopService {
    @Override
    public int insert(RedEnvelop redEnvelop) {
        this.baseMapper.insert(redEnvelop);
        return 1;
    }

    @Override
    public boolean updateById(RedEnvelop redEnvelop) {
        this.baseMapper.updateById(redEnvelop);
        return true;
    }

    @Override
    public RedEnvelop selectById(Integer id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public RedEnvelop selectOne(Wrapper<RedEnvelop> wrapper) {

        return this.baseMapper.selectOne(wrapper);
    }
    //更新红包数据
    @Override
    public String updateenvelop(RedEnvelop redEnvelop, double remainMoney, int remainSize) {
        redEnvelop.setRestmoney(remainMoney);
        redEnvelop.setRestcount(remainSize);
        updateById(redEnvelop);
        return "更新成功";
    }
}
