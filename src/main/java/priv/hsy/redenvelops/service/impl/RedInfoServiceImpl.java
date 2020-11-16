package priv.hsy.redenvelops.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.RedInfo;
import priv.hsy.redenvelops.entity.Result;
import priv.hsy.redenvelops.enums.ResultEnum;
import priv.hsy.redenvelops.mapper.RedInfoMapper;
import priv.hsy.redenvelops.service.RedInfoService;
import priv.hsy.redenvelops.utils.ResultUtil;

import java.util.List;

@Service
public class RedInfoServiceImpl extends ServiceImpl<RedInfoMapper, RedInfo> implements RedInfoService {


    @Override
    public List<RedInfo> selectAll() {
        return this.baseMapper.selectList(null);
    }

    @Override
    public int insert(RedInfo redInfo) {
        this.baseMapper.insert(redInfo);
        return 0;
    }

    /**
     * 将红包信息存入数据库
     */
    @Override
    public Result<Object> setred(RedInfo redInfo) {
        insert(redInfo);
        return ResultUtil.result(ResultEnum.REDSET_SUCCESS);
    }
}
