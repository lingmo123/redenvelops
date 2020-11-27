package priv.hsy.redenvelops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priv.hsy.redenvelops.entity.RedEnvelop;
import priv.hsy.redenvelops.entity.RedEnvelopPageBean;
import priv.hsy.redenvelops.entity.Result;
import priv.hsy.redenvelops.entity.User;
import priv.hsy.redenvelops.enums.ResultEnum;
import priv.hsy.redenvelops.mapper.RedEnvelopMapper;
import priv.hsy.redenvelops.service.RedEnvelopService;
import priv.hsy.redenvelops.service.RedisService;
import priv.hsy.redenvelops.service.UserService;
import priv.hsy.redenvelops.utils.ArithmeticUtils;
import priv.hsy.redenvelops.utils.ResultUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RedEnvelopServiceImpl extends ServiceImpl<RedEnvelopMapper, RedEnvelop> implements RedEnvelopService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private UserService userService;

    @Override
    public RedEnvelop selectById(BigInteger rid) {
        return this.baseMapper.selectById(rid);
    }

    /**
     * 更新正在抢的红包数据
     *
     * @param rid 红包id
     * @return
     */
    @Override
    public boolean update(BigInteger rid, Double money) {

        RedEnvelop redEnvelop = new RedEnvelop();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        UpdateWrapper<RedEnvelop> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("rest_count = rest_count - 1");
        updateWrapper.setSql("rest_money = convert(rest_money+" + money + ",decimal(18,2))");
        updateWrapper.setSql("rest_money = rest_money -" + money);
        updateWrapper.setSql("update_time = " + "'" + time + "'");
        updateWrapper.eq("rid", rid);

        this.baseMapper.update(redEnvelop, updateWrapper);
        return true;
    }

    /**
     * 更新编辑的红包数据至数据库
     *
     * @param redEnvelop 红包实体类
     * @param count      红包数量
     * @param totalMoney 红包金额
     * @return
     */
    @Override
    public String updateEnvelop(RedEnvelop redEnvelop, int count, String totalMoney) {
        Timestamp time = new Timestamp(System.currentTimeMillis());
        redEnvelop.setUpdateTime(time);
        redEnvelop.setRestMoney(totalMoney);
        redEnvelop.setRestCount(count);
        redEnvelop.setTotalMoney(totalMoney);
        redEnvelop.setCount(count);
        updateById(redEnvelop);
        return "更新成功";
    }

    /**
     * 发送红包
     *
     * @param rid 红包id
     * @return
     */
    @Override
    public Result<Object> sendRed(BigInteger rid) {
        RedEnvelop redEnvelop;
        redEnvelop = selectById(rid);
        redEnvelop.setStatus(1);
        Timestamp time = new Timestamp(System.currentTimeMillis());
        redEnvelop.setSendTime(time);
        try {
            redisService.redRedisIndex(redEnvelop.getRid(), redEnvelop.getCount(), redEnvelop.getTotalMoney());
        } catch (Exception e) {
            log.info("redis设置红包列表出错！");
            return ResultUtil.result(ResultEnum.FAIL, "发送失败！");
        }
        try {
            this.baseMapper.updateById(redEnvelop);
        } catch (Exception e) {
            log.info("更新数据库失败！");
            return ResultUtil.result(ResultEnum.FAIL, "发送失败！");
        }
        return ResultUtil.result(ResultEnum.SUCCESS, "发送成功！！");
    }

    /**
     * 分页查询
     *
     * @param currentPage 当前页
     * @param pageSize    每页大小
     * @return
     */
    @Override
    public Result<Object> selectPage(int currentPage, int pageSize, QueryWrapper<RedEnvelop> wrapper) {
        RedEnvelopPageBean redEnvelopPageBean = new RedEnvelopPageBean();
        redEnvelopPageBean.setPage(currentPage);
        int total = this.baseMapper.selectCount(wrapper);
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
        try {
            List<RedEnvelop> redInfoList = this.baseMapper.selectPage(page1, wrapper).getRecords();
            redEnvelopPageBean.setPageRecode(redInfoList);
            return ResultUtil.result(ResultEnum.SUCCESS, redEnvelopPageBean);
        } catch (Exception e) {
            log.info("error:{}", e);
            return ResultUtil.result(ResultEnum.FAIL, "获取列表失败");
        }
    }

    /**
     * 创建红包并插入到数据库
     *
     * @param redEnvelop 红包实体类
     * @return
     */
    @Override
    public Result<Object> setRed(RedEnvelop redEnvelop) {
        User user = userService.selectById(redEnvelop.getSendId());
        String userMoney = user.getMoney();
        String totalMoney = redEnvelop.getTotalMoney();
        //用户余额是否充足
        if (userMoney.compareTo(totalMoney) < 0) {
            return ResultUtil.result(ResultEnum.USERMONEY_NO);
        } else {
            BigDecimal restMoney = ArithmeticUtils.sub(userMoney, totalMoney);
            user.setMoney(restMoney.toString());
            userService.updateById(user);

            Timestamp time = new Timestamp(System.currentTimeMillis());
            redEnvelop.setCreateTime(time);
            redEnvelop.setRestCount(redEnvelop.getCount());
            redEnvelop.setRestMoney(redEnvelop.getTotalMoney());
            this.baseMapper.insert(redEnvelop);
            return ResultUtil.result(ResultEnum.REDSET_SUCCESS);
        }
    }

    /**
     * 编辑红包并更新数据库
     *
     * @param envelop 红包实体类
     * @return
     */
    @Override
    public Result<Object> updateRed(RedEnvelop envelop) {
        RedEnvelop redEnvelop = selectById(envelop.getRid());
        User user = userService.selectById(redEnvelop.getSendId());
        String userMoney = user.getMoney();
        String totalMoney = redEnvelop.getTotalMoney();
        String totalMoneyNow = envelop.getTotalMoney();

        if (Double.parseDouble(totalMoneyNow) > Double.parseDouble(totalMoney) + Double.parseDouble(userMoney)) {
            return ResultUtil.result(ResultEnum.USERMONEY_NO);
        } else {
            //红包信息更新
            updateEnvelop(redEnvelop, envelop.getCount(), totalMoneyNow);
            //发红包用户余额更新
            BigDecimal restMoney = ArithmeticUtils
                    .add(ArithmeticUtils.sub(totalMoneyNow, totalMoney).toString(), userMoney);
            user.setUid(redEnvelop.getSendId());
            user.setMoney(restMoney.toString());
            userService.updateById(user);
            return ResultUtil.result(ResultEnum.SUCCESS, "更新成功！");
        }

    }

    /**
     * 红包抢完后更改红包状态为抢完
     *
     * @param rid 红包id
     * @return
     */
    @Override
    public String overRed(BigInteger rid) {
        RedEnvelop redEnvelop;
        redEnvelop = selectById(rid);
        redEnvelop.setStatus(2);
        this.baseMapper.updateById(redEnvelop);
        return "红包已经抢完";
    }

}
