package priv.hsy.redenvelops.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.RedEnvelop;
import priv.hsy.redenvelops.entity.RedEnvelopPageBean;
import priv.hsy.redenvelops.entity.RedInfo;
import priv.hsy.redenvelops.entity.RedEnvelopPageBean;
import priv.hsy.redenvelops.mapper.RedEnvelopMapper;
import priv.hsy.redenvelops.service.RedEnvelopService;
import priv.hsy.redenvelops.service.RedisService;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RedEnvelopServiceImpl extends ServiceImpl<RedEnvelopMapper, RedEnvelop> implements RedEnvelopService {

    @Autowired
    private RedisService redisService;

    @Override
    public List<RedEnvelop> selectAll() {

        return this.baseMapper.selectList(null);
    }

    /**
     * 发送红包的红包详情存入数据库，并设置每个红包金额
     *
     * @param redEnvelop 实体类
     * @return
     */
    @Override
    public String insert(RedEnvelop redEnvelop) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        redEnvelop.setSendTime(time);
        this.baseMapper.insert(redEnvelop);
        try {
            redisService.redRedisIndex(redEnvelop.getRid(), redEnvelop.getCount(), redEnvelop.getTotalMoney());
        } catch (Exception e) {
            return "redis设置红包列表出错！";
        }
        return "success！";
    }

    /**
     * 更新正在抢的红包数据
     *
     * @param rid 红包id
     * @return
     */
    @Override
    public boolean update(Integer rid) {
        RedEnvelop redEnvelop = new RedEnvelop();
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.setSql("rest_count = rest_count - 1");
        updateWrapper.eq("rid",rid);

        this.baseMapper.update(redEnvelop,updateWrapper);
        return true;
    }

    /**
     * 数据库查询在抢红包
     *
     * @param wrapper 条件语句
     * @return
     */
    @Override
    public RedEnvelop selectOne(Wrapper<RedEnvelop> wrapper) {

        return this.baseMapper.selectOne(wrapper);
    }


    /**
     * 更新红包数据
     *
     * @param redEnvelop  实体类
     * @param remainMoney 红包剩余金额
     * @param remainSize  红包剩余数量
     * @return
     */
    @Override
    public String updateEnvelop(RedEnvelop redEnvelop, double remainMoney, int remainSize) {
        redEnvelop.setRestMoney(remainMoney);
        redEnvelop.setRestCount(remainSize);
        updateById(redEnvelop);
        return "更新成功";
    }

    @Override
    public RedEnvelopPageBean selectPage(int currentPage, int pageSize) {
        RedEnvelopPageBean redEnvelopPageBean = new RedEnvelopPageBean();
        redEnvelopPageBean.setPage(currentPage);
        int total = this.baseMapper.selectCount(null);
        redEnvelopPageBean.setTotal(total);
        int totalPage;
        redEnvelopPageBean.setLimit(pageSize);
        if (total % pageSize == 0) {
            totalPage = total / pageSize;
        } else {
            totalPage = total / pageSize + 1;
        }
        redEnvelopPageBean.setTotalPage(totalPage);
        List<Integer> pages = new ArrayList<>();
        for (int i = 1; i < totalPage + 1; i++) {
            pages.add(i);
        }
        redEnvelopPageBean.setPages(pages);
        //page是当前页，limit是每页多少数据
        Page<RedEnvelop> page1 = new Page<>(currentPage, pageSize);
        List<RedEnvelop> redInfoList = this.baseMapper.selectPage(page1, null).getRecords();
        redEnvelopPageBean.setPageRecode(redInfoList);

        return redEnvelopPageBean;
    }

}
