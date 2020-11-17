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
    /**
     * 查询数据库中此id是否抢过红包
     * @param wrapper 条件语句
     * @return 查询结果
     */
    @Override
    public RedDetail selectOne(Wrapper<RedDetail> wrapper) {

        return this.baseMapper.selectOne(wrapper);
    }

    /**
     * 抢红包信息插入数据库
     *
     * @param redDetail 实体类
     * @return
     */
    @Override
    public String insert(RedDetail redDetail) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        redDetail.setGetTime(time);
        this.baseMapper.insert(redDetail);
        return "success";
    }

    /**
     * 更新抢红包明细
     *
     * @param redEnvelop 实体类
     * @param money 抢到的红包金额
     * @param rid 所抢红包id
     * @param id 用户id
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
