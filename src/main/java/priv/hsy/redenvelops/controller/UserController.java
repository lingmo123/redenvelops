package priv.hsy.redenvelops.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import priv.hsy.redenvelops.entity.*;
import priv.hsy.redenvelops.enums.ResultEnum;
import priv.hsy.redenvelops.service.RedDetailService;
import priv.hsy.redenvelops.service.RedEnvelopService;
import priv.hsy.redenvelops.service.RedInfoService;
import priv.hsy.redenvelops.service.UserService;
import priv.hsy.redenvelops.utils.ResultUtil;

import java.security.Key;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private RedInfoService redInfoService;
    @Autowired
    private RedEnvelopService redEnvelopService;
    @Autowired
    private RedDetailService redDetailService;

    @GetMapping(value = "/getuser")
    public List<User> getuser() {
        List<User> userList = userService.selectAll();
        log.info("userList = [{}]", userList);
        return userList;
    }

    @GetMapping(value = "/api/getredinfo")
    public List<RedInfo> getredinfo() {
        List<RedInfo> redinfoList = redInfoService.selectAll();
        log.info("userList = [{}]", redinfoList);
        return redinfoList;
    }

    /**
     * 设置红包接口
     */
    @PostMapping(value = "/api/setred")
    public Result<Object> setred(@RequestBody RedInfo redInfo) {
        User user = userService.selectById(redInfo.getSendid());
        double money = user.getMoney();
        //红包金额下限
        if (redInfo.getTotalmoney() < 0.01) {
//            return "红包金额最小值为0.01";
            return ResultUtil.result(ResultEnum.REDMONEY_MIN);
            //红包数量下限
        } else if (redInfo.getCount() < 1) {
//            return "红包数量最小为1";
            return ResultUtil.result(ResultEnum.REDCOUNT_MIN);
            //用户余额是否不足
        } else if (money < redInfo.getTotalmoney()) {
//            return "你的余额不足，无法设置红包，请加班挣钱！";
            return ResultUtil.result(ResultEnum.USERMONEY_NO);
        } else {//发红包用户余额更新
            double restmoney = money - redInfo.getTotalmoney();
            User user1 = new User();
            user1.setUid(1);
            user1.setMoney(restmoney);
            userService.updateById(user1);
            return redInfoService.setred(redInfo);
        }
    }

    @PostMapping(value = "/api/sendred")
    public Result<Object> sendred(@RequestBody RedInfo redInfo) {

        RedEnvelop redEnvelop = new RedEnvelop();
        redEnvelop.setCount(redInfo.getCount());
        //设置剩余红包个数
        redEnvelop.setRestcount(redInfo.getCount());
        redEnvelop.setRid(redInfo.getRid());
        redEnvelop.setTotalmoney(redInfo.getTotalmoney());
        //设置剩余金额
        redEnvelop.setRestmoney(redInfo.getTotalmoney());
        redEnvelop.setSendid(redInfo.getSendid());
        redEnvelop.setStatus(true);
        redEnvelopService.insert(redEnvelop);
        //return "成功发送红包！"
        return ResultUtil.result(ResultEnum.REDSEND_SUCCESS);
    }

    @PostMapping(value = "/api/getred")
    public Result<Object> getred(@RequestParam("rid") Integer rid, @RequestParam("id") Integer id) {

        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper
                .eq("rid", rid);
        //获得所抢红包信息
        RedEnvelop redEnvelopcurrent = redEnvelopService.selectOne(wrapper);

        if (redEnvelopcurrent.getStatus().equals(false)) {
//            return "红包还未开始抢";
            return null;
        } else if (redEnvelopcurrent.getRestcount() < 0) {

//            return "红包已经抢完了";
            return ResultUtil.result(ResultEnum.REDCOUNT_NO);
        } else {//判断该红包此id是否已经抢过
            QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.and(i -> i
                    .eq("rid", rid)
                    .eq("receiveid", id));
            if (redDetailService.selectOne(queryWrapper) != null) {
//                return "您已经抢过红包了";
                return ResultUtil.result(ResultEnum.USERGET_NO);
            } else {
                String key = redEnvelopcurrent.getRid()+"redmoneylist";
                //获得此次红包金额
                int remainSize = redEnvelopcurrent.getRestcount();
                Double remainMoney = redEnvelopcurrent.getRestmoney();
                double money = (double) redisTemplate.boundListOps(key).rightPop();
                log.info("money = ", money);
                //用户抢到红包更新账户余额
                userService.updateUserifo(id, money);
                //更新红包数据
                remainSize--;
                remainMoney -= money;
                redEnvelopService.updateenvelop(redEnvelopcurrent, remainMoney, remainSize);
                //更新红包明细
                redDetailService.updateRedDetail(redEnvelopcurrent, money, rid, id);
                //return 成功抢到红包
                return ResultUtil.result(ResultEnum.REDGET_SUCCESS);
            }
        }
    }

    @PostMapping(value = "/test")
    public Result<Object> test(@RequestParam("rid") Integer rid) {

        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper
                .eq("rid", rid);
        //获得所抢红包信息
        RedEnvelop redEnvelopcurrent = redEnvelopService.selectOne(wrapper);

        if (redEnvelopcurrent.getStatus().equals(false)) {
//            return "红包还未开始抢";
            return null;
        } else if (redEnvelopcurrent.getRestcount() < 0) {

//            return "红包已经抢完了";
            return ResultUtil.result(ResultEnum.REDCOUNT_NO);
        } else {
                String key = redEnvelopcurrent.getRid()+"redmoneylist";
                //获得此次红包金额
                int remainSize = redEnvelopcurrent.getRestcount();
                Double remainMoney = redEnvelopcurrent.getRestmoney();
                try {
                    double money = (double) redisTemplate.boundListOps(key).rightPop();
                    log.info("money = {}", money);
                }
                catch (Exception e){
                    return ResultUtil.result(ResultEnum.FAIL);
                }
                return ResultUtil.result(ResultEnum.SUCCESS);
                //用户抢到红包更新账户余额
//                userService.updateUserifo(id, money);
                //更新红包数据
//                remainSize--;
//                remainMoney -= money;
//                redEnvelopService.updateenvelop(redEnvelopcurrent, remainMoney, remainSize);
//                //更新红包明细
//                redDetailService.updateRedDetail(redEnvelopcurrent, money, rid,2);
//                //return 成功抢到红包
//                return ResultUtil.result(ResultEnum.REDGET_SUCCESS);
            }
    }

    @PostMapping(value = "/test1")
    public Result<Object> test1(@RequestParam("rid") Integer rid,@RequestParam("id") Integer id) {

        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper
                .eq("rid", rid);
        //获得所抢红包信息
        RedEnvelop redEnvelopcurrent = redEnvelopService.selectOne(wrapper);

        if (redEnvelopcurrent.getStatus().equals(false)) {
//            return "红包还未开始抢";
            return null;
        } else if (redEnvelopcurrent.getRestcount() < 0) {

//            return "红包已经抢完了";
            return ResultUtil.result(ResultEnum.REDCOUNT_NO);
        } else {//判断该红包此id是否已经抢过
            QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.and(i -> i
                    .eq("rid", rid)
                    .eq("receiveid", id));
            if (redDetailService.selectOne(queryWrapper) != null) {
//                return "您已经抢过红包了";
                return ResultUtil.result(ResultEnum.USERGET_NO);
            }else {
                String key = redEnvelopcurrent.getRid() + "redmoneylist";
                //获得此次红包金额
                int remainSize = redEnvelopcurrent.getRestcount();
                Double remainMoney = redEnvelopcurrent.getRestmoney();
                try {
                    double money = (double) redisTemplate.boundListOps(key).rightPop();
                    log.info("money = {}", money);
                    //用户抢到红包更新账户余额
                    userService.updateUserifo(id, money);
                    //更新红包数据
                    remainSize--;
                    remainMoney -= money;
                    redEnvelopService.updateenvelop(redEnvelopcurrent, remainMoney, remainSize);
                    //更新红包明细
                    redDetailService.updateRedDetail(redEnvelopcurrent, money, rid, id);
                    //return 成功抢到红包
                    return ResultUtil.result(ResultEnum.REDGET_SUCCESS);
                } catch (Exception e) {
                    return ResultUtil.result(ResultEnum.REDCOUNT_NO);
                }
            }
        }
    }
}

