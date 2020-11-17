package priv.hsy.redenvelops.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.PageBean;
import priv.hsy.redenvelops.entity.RedInfo;
import priv.hsy.redenvelops.entity.Result;
import priv.hsy.redenvelops.enums.ResultEnum;
import priv.hsy.redenvelops.mapper.RedInfoMapper;
import priv.hsy.redenvelops.service.RedInfoService;
import priv.hsy.redenvelops.utils.ResultUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
public class RedInfoServiceImpl extends ServiceImpl<RedInfoMapper, RedInfo> implements RedInfoService {

    @Override
    public List<RedInfo> select() {
        return this.baseMapper.selectList(null);
    }

    @Override
    public PageBean selectPage(int page) {
        PageBean pageBean = new PageBean();
        pageBean.setPage(page);
        int total = this.baseMapper.selectCount(null);
        pageBean.setTotal(total);
        int totalPage;
        int limit = 5;
        pageBean.setLimit(limit);
        if (total % limit == 0) {
            totalPage = total / limit;
        } else {
            totalPage = total / limit + 1;
        }
        pageBean.setTotalPage(totalPage);
        List<Integer> pages = new ArrayList<>();
        for(int i=1;i<totalPage+1;i++) {
            pages.add(i);
        }
        pageBean.setPages(pages);
        //page是当前页，limit是每页多少数据
        Page<RedInfo> page1 = new Page<>(page,limit);
        List<RedInfo> redInfoList=this.baseMapper.selectPage(page1, null).getRecords();
        pageBean.setPageRecode(redInfoList);

        return pageBean;
    }

    @Override
    public int insert(RedInfo redInfo) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        redInfo.setCreateTime(time);
        this.baseMapper.insert(redInfo);
        return 0;
    }

    /**
     * 将红包信息存入数据库
     */
    @Override
    public Result<Object> setRed(RedInfo redInfo) {
        insert(redInfo);
        return ResultUtil.result(ResultEnum.REDSET_SUCCESS);
    }


}
