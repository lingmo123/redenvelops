package priv.hsy.redenvelops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.RedDetail;
import priv.hsy.redenvelops.entity.Result;
import priv.hsy.redenvelops.enums.ResultEnum;
import priv.hsy.redenvelops.mapper.RedDtailMapper;
import priv.hsy.redenvelops.service.RedDetailService;
import priv.hsy.redenvelops.utils.ResultUtil;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

/**
*
* @author hsy
* @date 2020/12/16 9:44
*/
@Service
public class RedDetailServiceImpl extends ServiceImpl<RedDtailMapper, RedDetail> implements RedDetailService {

    @Override
    public RedDetail selectOne(Wrapper<RedDetail> wrapper) {
        return this.baseMapper.selectOne(wrapper);
    }

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

    @Override
    public Result<Object> selectDetails(BigInteger rid) {
        QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("rid", rid);
        try {
            List<RedDetail> redDetails = this.baseMapper.selectList(queryWrapper);
            return ResultUtil.result(ResultEnum.SUCCESS, redDetails);
        } catch (Exception e) {
            return ResultUtil.result(ResultEnum.FAIL);
        }
    }

    @Override
    public Result<Object> userRedDetails(BigInteger uid) {
        QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq("receive_id", uid);
        List<RedDetail> detailList = this.baseMapper.selectList(queryWrapper);
        if (detailList == null) {
            return ResultUtil.result(ResultEnum.RED_DETAIL_FAIL);
        }
        return ResultUtil.result(ResultEnum.SUCCESS, detailList);
    }


}
