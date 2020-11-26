package priv.hsy.redenvelops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.RedDetail;
import priv.hsy.redenvelops.mapper.RedDtailMapper;
import priv.hsy.redenvelops.service.RedDetailService;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

@Service
public class RedDetailServiceImpl extends ServiceImpl<RedDtailMapper, RedDetail> implements RedDetailService {
    /**
     * 查询数据库中此id是否抢过红包
     *
     * @param wrapper 条件语句
     * @return 查询结果
     */
    @Override
    public RedDetail selectOne(Wrapper<RedDetail> wrapper) {
        return this.baseMapper.selectOne(wrapper);
    }

    /**
     * 更新抢红包明细
     *
     * @param uid   抢红包用户id
     * @param rid   红包id
     * @param money 所抢到的金额
     * @return
     */
    @Override
    public String insert(BigInteger uid, BigInteger rid, double money) {
        RedDetail redDetail = new RedDetail();
        redDetail.setRid(rid);
        redDetail.setReceiveId(uid);
        redDetail.setGetMoney(money);
        Timestamp time = new Timestamp(System.currentTimeMillis());
        redDetail.setGetTime(time);
        this.baseMapper.insert(redDetail);
        return "success";
    }

    /**
     * 在数据库中查找指定红包ID的明细
     *
     * @param rid 红包id
     * @return
     */
    @Override
    public List<RedDetail> selectDetails(BigInteger rid) {
        QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("rid", rid);
        return this.baseMapper.selectList(queryWrapper);
    }
}
