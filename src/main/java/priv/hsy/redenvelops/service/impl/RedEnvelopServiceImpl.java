package priv.hsy.redenvelops.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import priv.hsy.redenvelops.entity.RedEnvelop;
import priv.hsy.redenvelops.api.dto.*;
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

    @Transactional( rollbackFor = Exception.class)
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

    @Override
    public Result<Object> selectPage(int currentPage, int pageSize, QueryWrapper<RedEnvelop> wrapper) {
        RedEnvelopPageDto redEnvelopPageDto = new RedEnvelopPageDto();
        redEnvelopPageDto.setPage(currentPage);
        int total = this.baseMapper.selectCount(wrapper);
        redEnvelopPageDto.setTotal(total);
        int totalPage;
        redEnvelopPageDto.setLimit(pageSize);
        if (total % pageSize == 0) {
            totalPage = total / pageSize;
        } else {
            totalPage = total / pageSize + 1;
        }
        redEnvelopPageDto.setTotalPage(totalPage);
        List<Integer> pages = new ArrayList<>();
        for (int i = 1; i < totalPage + 1; i++) {
            pages.add(i);
        }
        redEnvelopPageDto.setPages(pages);
        //page是当前页，limit是每页多少数据
        Page<RedEnvelop> page1 = new Page<>(currentPage, pageSize);
        try {
            List<RedEnvelop> redInfoList = this.baseMapper.selectPage(page1, wrapper).getRecords();
            redEnvelopPageDto.setPageRecode(redInfoList);
            return ResultUtil.result(ResultEnum.SUCCESS, redEnvelopPageDto);
        } catch (Exception e) {
            log.info("error:{}", e);
            return ResultUtil.result(ResultEnum.FAIL, "获取列表失败");
        }
    }

    @Transactional( rollbackFor = Exception.class)
    @Override
    public Result<Object> setRed(RedEnvelop redEnvelop) {
        User user = userService.selectById(redEnvelop.getSendId());
        String userMoney = user.getMoney();
        String totalMoney = redEnvelop.getTotalMoney();
        //用户余额是否充足
        if (userMoney.compareTo(totalMoney) < 0) {
            return ResultUtil.result(ResultEnum.USER_MONEY_NO);
        } else {
            Timestamp time = new Timestamp(System.currentTimeMillis());
            redEnvelop.setCreateTime(time);
            redEnvelop.setRestCount(redEnvelop.getCount());
            redEnvelop.setRestMoney(redEnvelop.getTotalMoney());
            this.baseMapper.insert(redEnvelop);

            BigDecimal restMoney = ArithmeticUtils.sub(userMoney, totalMoney);
            user.setMoney(restMoney.toString());
            userService.updateById(user);
            return ResultUtil.result(ResultEnum.RED_SET_SUCCESS);
        }
    }

    @Transactional( rollbackFor = Exception.class)
    @Override
    public Result<Object> updateRed(RedEnvelop envelop) {
        RedEnvelop redEnvelop = selectById(envelop.getRid());
        User user = userService.selectById(redEnvelop.getSendId());
        String userMoney = user.getMoney();
        String totalMoney = redEnvelop.getTotalMoney();
        String totalMoneyNow = envelop.getTotalMoney();

        if (Double.parseDouble(totalMoneyNow) > Double.parseDouble(totalMoney) + Double.parseDouble(userMoney)) {
            return ResultUtil.result(ResultEnum.USER_MONEY_NO);
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

    @Override
    public String overRed(BigInteger rid) {
        RedEnvelop redEnvelop;
        redEnvelop = selectById(rid);
        redEnvelop.setStatus(2);
        this.baseMapper.updateById(redEnvelop);
        return "红包已经抢完";
    }

    @Override
    public Result<Object> selectAllRed() {
        RedEnvelopDetailsDto dto = new RedEnvelopDetailsDto();
        List<RedEnvelop> list;
        list = this.baseMapper.selectList(null);
        if (list == null) {
            return ResultUtil.result(ResultEnum.DATA_SELECT_NULL);
        }
        try{
            int redNotCount = 0;
            int redSnatchCount = 0;
            int redOverCount = 0;
            String totalMoney = "0";
            BigDecimal result;
            String snatchMoney = "0";
            BigDecimal result1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getStatus() == 0) {
                    redNotCount++;
                }
                if (list.get(i).getStatus() == 1) {
                    redSnatchCount++;
                }
                if (list.get(i).getStatus() == 2) {
                    redOverCount++;
                }
                result = ArithmeticUtils.add(totalMoney, list.get(i).getTotalMoney());
                result1 = ArithmeticUtils.add(snatchMoney, list.get(i).getRestMoney());
                totalMoney = result.toString();
                snatchMoney = result1.toString();

            }

            dto.setTotalRedCount(list.size());
            dto.setRedNotCount(redNotCount);
            dto.setRedSnatchCount(redSnatchCount);
            dto.setRedOverCount(redOverCount);
            dto.setTotalMoney(totalMoney);
            dto.setSnatchMoney(snatchMoney);
        }catch (NullPointerException e){
            return ResultUtil.result(ResultEnum.FAIL);

        }
        return ResultUtil.result(ResultEnum.SUCCESS, dto);
    }
    @Override
    @Transactional( rollbackFor = Exception.class)
    public String test() {
        RedEnvelop redEnvelop = new RedEnvelop();
        UpdateWrapper<RedEnvelop> updateWrapper = new UpdateWrapper<>();
        updateWrapper.setSql("rest_money = rest_money -" + 10);
        updateWrapper.eq("rid", 470);
        this.baseMapper.update(redEnvelop,updateWrapper);
        int i=10;
        i=i/0;
        UpdateWrapper<RedEnvelop> updateWrapper1 = new UpdateWrapper<>();
        updateWrapper1.setSql("rest_money = rest_money +" + 5);
        updateWrapper1.eq("rid", 470);
        this.baseMapper.update(redEnvelop,updateWrapper1);

        return null;
    }

}
