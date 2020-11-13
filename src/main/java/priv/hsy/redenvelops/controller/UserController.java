package priv.hsy.redenvelops.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priv.hsy.redenvelops.entity.*;
import priv.hsy.redenvelops.service.RedDetailService;
import priv.hsy.redenvelops.service.RedEnvelopService;
import priv.hsy.redenvelops.service.RedInfoService;
import priv.hsy.redenvelops.service.UserService;
import priv.hsy.redenvelops.utils.GetMoney;

import java.util.List;

@RestController
@Slf4j
public class UserController {
    /**
     * 设置每个红包最小金额
     */
    private static final double MIN = 0.01;
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

    @GetMapping(value = "/getredinfo")
    public List<RedInfo> getredinfo() {
        List<RedInfo> redinfoList = redInfoService.selectAll();
        log.info("userList = [{}]", redinfoList);
        return redinfoList;
    }

    //设置红包
    @PostMapping(value = "/api/redset")
    public String redset(@RequestBody RedInfo redInfo) {
        User user = userService.selectById(redInfo.getSendid());
        double money = user.getMoney();
        if (redInfo.getTotalmoney() < MIN) {
            return "红包金额最小值为0.01";
        } else if (redInfo.getCount() < 1) {
            return "红包数量最小为1";
        } else if (money < redInfo.getTotalmoney()) {
            return "尊敬的打工人你的余额不足，无法设置红包，请加班挣钱！";
        } else {//发红包用户余额更新


            double restmoney = money - redInfo.getTotalmoney();
            User user1 = new User();
            user1.setUid(1);
            user1.setMoney(restmoney);
            userService.updateById(user1);
            return redInfoService.setred(redInfo);
        }
    }

    @PostMapping(value = "/redsend")
    public String redsend(@RequestBody RedInfo redInfo) {

        RedEnvelop redEnvelop = new RedEnvelop();
        redEnvelop.setCount(redInfo.getCount());
        redEnvelop.setRestcount(redInfo.getCount());//设置剩余红包个数
        redEnvelop.setRid(redInfo.getRid());
        redEnvelop.setTotalmoney(redInfo.getTotalmoney());
        redEnvelop.setRestmoney(redInfo.getTotalmoney());//设置剩余金额
        redEnvelop.setSendid(redInfo.getSendid());
        redEnvelop.setStatus(true);
        redEnvelopService.insert(redEnvelop);
        return "发送成功！";
    }

    @PostMapping(value = "/redget")
    public String redget(@RequestBody Bean bean) {
//        RedEnvelop redEnvelop = bean.getRedEnvelop();
        QueryWrapper<RedEnvelop> Wrapper = new QueryWrapper<>();
        Wrapper
                .eq("rid", bean.getRedEnvelop().getRid());
        //获得所抢红包信息
        RedEnvelop redEnvelopcurrent = redEnvelopService.selectOne(Wrapper);
        int id = bean.getId();
        if (!redEnvelopcurrent.getStatus()) {
            return "红包还未开始抢";
        } else if (redEnvelopcurrent.getRestcount() < 0) {
            return "红包已经抢完了";
        } else {//判断该红包此id是否已经抢过
            int rid = redEnvelopcurrent.getRid();
            QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.and(i -> i
                    .eq("rid", rid)
                    .eq("receiveid", id));
            if (redDetailService.selectOne(queryWrapper) != null) {
                return "您已经抢过红包了";
            } else {
                //获得此次红包金额
                int remainSize = redEnvelopcurrent.getRestcount();
                Double remainMoney = redEnvelopcurrent.getRestmoney();
                double money = GetMoney.getRandomMoney(remainSize, remainMoney);
                //用户抢到红包更新账户余额
                userService.updateUserifo(id, money);
                //更新红包数据
                remainSize--;
                remainMoney -= money;
                redEnvelopService.updateenvelop(redEnvelopcurrent, remainMoney, remainSize);
                //更新红包明细
                redDetailService.updateRedDetail(redEnvelopcurrent, money, rid, id);
                return "成功抢到";
            }
        }

    }

}

