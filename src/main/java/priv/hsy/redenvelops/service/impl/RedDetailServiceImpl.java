package priv.hsy.redenvelops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.RedDetail;
import priv.hsy.redenvelops.entity.RedEnvelop;
import priv.hsy.redenvelops.mapper.RedDtailMapper;
import priv.hsy.redenvelops.service.RedDetailService;

import java.sql.Timestamp;


@Service
public class RedDetailServiceImpl extends ServiceImpl<RedDtailMapper, RedDetail> implements RedDetailService {

    @Override
    public RedDetail selectOne(Wrapper<RedDetail> wrapper) {

        return this.baseMapper.selectOne(wrapper);
    }

    /**
     * 抢红包信息插入数据库
     *
     * @param redDetail
     * @return
     */
    @Override
    public int insert(RedDetail redDetail) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        redDetail.setGetTime(time);
        this.baseMapper.insert(redDetail);
        return 1;
    }

    /**
     * 更新抢红包明细
     *
     * @param redEnvelop
     * @param money
     * @param rid
     * @param id
     * @return
     */
    @Override
    public String updateRedDetail(RedEnvelop redEnvelop, double money, int rid, int id) {
        RedDetail redDetail = new RedDetail();
        redDetail.setRid(redEnvelop.getRid());
        redDetail.setGetMoney(money);
        redDetail.setReceiveId(id);
        insert(redDetail);
        return "更新红包明细成功";
    }
}
