package priv.hsy.redenvelops.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

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
    public List<User> getUser() {
        List<User> userList = userService.selectAll();
        log.info("userList = [{}]", userList);
        return userList;
    }

    /**
     * 获取红包详情
     *
     * @return 返回状态码，消息提示以及设置的红包详情
     */
    @GetMapping(value = "/api/getredinfo")
    public Result<Object> getRedInfo() {
        List<RedInfo> redInfoList = redInfoService.select();
        return ResultUtil.result(ResultEnum.SUCCESS, redInfoList);
    }

    /**
     * 分页显示所有设置红包详情
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/api/getpageredinfo")
    public Result<Object> getPageRedInfo(@RequestParam("currentPage") Integer currentPage,
                                         @RequestParam("pageSize") Integer pageSize) {

        PageBean pageBean = redInfoService.selectPage(currentPage, pageSize);
        log.info("list = [{}]", pageBean.getPageRecode());
        return ResultUtil.result(ResultEnum.SUCCESS, pageBean);
    }

    @GetMapping(value = "/api/getredenvelop")
    public Result<Object> getRedEnvelop() {
        List<RedEnvelop> redEnvelopList = redEnvelopService.selectAll();
        return ResultUtil.result(ResultEnum.SUCCESS, redEnvelopList);
    }

    /**
     * 设置红包接口
     *
     * @param redInfo 获取前端form表信息
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/setred")
    public Result<Object> setRed(@RequestBody RedInfo redInfo) {
        User user = userService.selectById(redInfo.getSendId());
        double money = user.getMoney();
        //红包金额下限
        if (redInfo.getTotalMoney() < 0.01) {
//            return "红包金额最小值为0.01";
            return ResultUtil.result(ResultEnum.REDMONEY_MIN);
            //红包数量下限
        } else if (redInfo.getCount() < 1) {
//            return "红包数量最小为1";
            return ResultUtil.result(ResultEnum.REDCOUNT_MIN);
            //用户余额是否不足
        } else if (money < redInfo.getTotalMoney()) {
//            return "你的余额不足，无法设置红包，请加班挣钱！";
            return ResultUtil.result(ResultEnum.USERMONEY_NO);
        } else {//发红包用户余额更新
            double restmoney = money - redInfo.getTotalMoney();
            User user1 = new User();
            user1.setUid(1);
            user1.setMoney(restmoney);
            userService.updateById(user1);
            return redInfoService.setRed(redInfo);
        }
    }

    /**
     * 编辑已设置的红包
     *
     * @param rid        红包ID
     * @param count      红包数量
     * @param totalMoney 红包金额
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/updatered")
    public Result<Object> updateRed(@RequestParam("rid") Integer rid, @RequestParam("count") Integer count,
                                    @RequestParam("totalMoney") Double totalMoney) {
        RedInfo redInfo = redInfoService.selectById(rid);
        User user = userService.selectById(redInfo.getSendId());
        if (totalMoney < 0.01) {
//            return "红包金额最小值为0.01";
            return ResultUtil.result(ResultEnum.REDMONEY_MIN);
            //红包数量下限
        } else if (count < 1) {
//            return "红包数量最小为1";
            return ResultUtil.result(ResultEnum.REDCOUNT_MIN);
            //用户余额是否不足
        } else if (totalMoney > redInfo.getTotalMoney() + user.getMoney()) {
//            return "你的余额不足，无法设置红包！";
            return ResultUtil.result(ResultEnum.USERMONEY_NO);
        } else {
            //红包信息更新
            double restmoney = (totalMoney - redInfo.getTotalMoney()) + user.getMoney();
            redInfoService.update(redInfo, rid, count, totalMoney);
            //发红包用户余额更新
            user.setUid(redInfo.getSendId());
            user.setMoney(restmoney);
            userService.updateById(user);
            return ResultUtil.result(ResultEnum.REDSET_SUCCESS);
        }
    }

    /**
     * 发送红包接口
     *
     * @param redInfo 获取前端红包信息
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/sendred")
    public Result<Object> sendRed(@RequestBody RedInfo redInfo) {

        RedEnvelop redEnvelop = new RedEnvelop();
        redEnvelop.setCount(redInfo.getCount());
        //设置剩余红包个数
        redEnvelop.setRestCount(redInfo.getCount());
        redEnvelop.setRid(redInfo.getRid());
        redEnvelop.setTotalMoney(redInfo.getTotalMoney());
        //设置剩余金额
        redEnvelop.setRestMoney(redInfo.getTotalMoney());
        redEnvelop.setSendId(redInfo.getSendId());
        redEnvelop.setStatus(true);

        redEnvelopService.insert(redEnvelop);
        //return "成功发送红包！"
        return ResultUtil.result(ResultEnum.REDSEND_SUCCESS);
    }

    /**
     * 抢红包接口
     *
     * @param rid 获取前端红包id
     * @param id  获取前端抢红包用户id
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/getred")
    public Result<Object> getRed(@RequestParam("rid") Integer rid, @RequestParam("id") Integer id) {

        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper
                .eq("rid", rid);
        //获得所抢红包信息
        RedEnvelop redEnvelopcurrent = redEnvelopService.selectOne(wrapper);

        if (redEnvelopcurrent.getStatus().equals(false)) {
//            return "红包还未开始抢";
            return null;
        } else if (redEnvelopcurrent.getRestCount() < 0) {

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
                String key = redEnvelopcurrent.getRid() + "redmoneylist";
                //获得此次红包金额
                int remainSize = redEnvelopcurrent.getRestCount();
                Double remainMoney = redEnvelopcurrent.getRestMoney();
                double money = (double) redisTemplate.boundListOps(key).rightPop();
                log.info("money = ", money);
                //用户抢到红包更新账户余额
                userService.updateUserifo(id, money);
                //更新红包数据
                remainSize--;
                remainMoney -= money;
                redEnvelopService.updateEnvelop(redEnvelopcurrent, remainMoney, remainSize);
                //更新红包明细
                redDetailService.updateRedDetail(redEnvelopcurrent, money, rid, id);
                //return 成功抢到红包
                return ResultUtil.result(ResultEnum.REDGET_SUCCESS);
            }
        }
    }

    @PostMapping(value = "/test")
    public Result<Object> test(@RequestParam("rid") Integer rid) {
        String key = rid + "redMoneylist";
        String redInfoCount = rid + "redInfoCount";
        String redInfoMoney = rid + "redInfoMoney";

        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "first", 2, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(lock)) {
            return ResultUtil.result(ResultEnum.FAIL, "lock");
        }
        try {
            double restMoney = (double) redisTemplate.opsForValue().get(redInfoMoney);
            Integer restSize = (int) redisTemplate.opsForValue().get(redInfoCount);

            log.info("restMoney = {}", restMoney);
            log.info("restSize = {}", restSize);
            if (restSize != null && restSize > 0) {
                redisTemplate.opsForValue().decrement(redInfoCount);
                Double money = (Double) redisTemplate.boundListOps(key).rightPop();
                if (money == null) {
                    return ResultUtil.result(ResultEnum.FAIL, "monry == null");
                }
                restMoney -= money;
                redisTemplate.opsForValue().set(redInfoMoney, restMoney);
                return ResultUtil.result(ResultEnum.REDGET_SUCCESS);
            } else {
                return ResultUtil.result(ResultEnum.REDCOUNT_NO);
            }
        } catch (Exception e) {
            return ResultUtil.result(ResultEnum.FAIL, "catch");
        } finally {
            redisTemplate.delete("lock");
        }
    }

    @PostMapping(value = "/test1")
    public Result<Object> test1(@RequestParam("rid") Integer rid, @RequestParam("id") Integer id) {

        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper
                .eq("rid", rid);
        //获得所抢红包信息
        RedEnvelop redEnvelopcurrent = redEnvelopService.selectOne(wrapper);

        if (redEnvelopcurrent.getStatus().equals(false)) {
//            return "红包还未开始抢";
            return null;
        } else if (redEnvelopcurrent.getRestCount() < 0) {

//            return "红包已经抢完了";
            return ResultUtil.result(ResultEnum.REDCOUNT_NO);
        } else {//判断该红包此id是否已经抢过
            QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.and(i -> i
                    .eq("rid", rid)
                    .eq("receive_id", id));
            if (redDetailService.selectOne(queryWrapper) != null) {
//                return "您已经抢过红包了";
                return ResultUtil.result(ResultEnum.USERGET_NO);
            } else {
                String key = redEnvelopcurrent.getRid() + "redmoneylist";
                //获得此次红包金额
                int remainSize = redEnvelopcurrent.getRestCount();
                Double remainMoney = redEnvelopcurrent.getRestMoney();
                try {
                    double money = (double) redisTemplate.boundListOps(key).rightPop();
                    log.info("money = {}", money);
                    //用户抢到红包更新账户余额
                    userService.updateUserifo(id, money);
                    //更新红包数据
                    remainMoney -= money;
                    remainSize--;
                    redEnvelopService.updateEnvelop(redEnvelopcurrent, remainMoney, remainSize);
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

    @PostMapping(value = "/test2")
    public Result<Object> test2(@RequestParam("rid") Integer rid, @RequestParam("id") Integer id) {

        QueryWrapper<RedEnvelop> wrapper = new QueryWrapper<>();
        wrapper
                .eq("rid", rid);
        //获得所抢红包信息
        RedEnvelop redEnvelopcurrent = redEnvelopService.selectOne(wrapper);

        if (redEnvelopcurrent.getStatus().equals(false)) {
//            return "红包还未开始抢";
            return null;
        } else {//判断该红包此id是否已经抢过
            QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
            queryWrapper.and(i -> i
                    .eq("rid", rid)
                    .eq("receive_id", id));
            if (redDetailService.selectOne(queryWrapper) != null) {
//                return "您已经抢过红包了";
                return ResultUtil.result(ResultEnum.USERGET_NO);
            } else {
                String key = redEnvelopcurrent.getRid() + "redmoneylist";
                String redinfokey = redEnvelopcurrent.getRid() + "redinfolist";
                try {
                    double restMoney = (double) redisTemplate.opsForList().index(redinfokey, 0);
                    int restSize = (int) redisTemplate.opsForList().index(redinfokey, 1);
//                    double restMoney = (double) redisTemplate.opsForList().index(key, 0);
//                    int restSize = (int) redisTemplate.opsForList().index(key, 1);
                    if (restMoney == 0) {
                        return ResultUtil.result(ResultEnum.REDCOUNT_NO);
                    }
                    if (restSize == 0) {
                        return ResultUtil.result(ResultEnum.REDCOUNT_NO);
                    } else {
                        try {
                            double money = (double) redisTemplate.boundListOps(key).rightPop();
                            restMoney -= money;
                            restSize--;
                            redisTemplate.opsForList().set(redinfokey, 0, restMoney);
                            redisTemplate.opsForList().set(redinfokey, 1, restSize);
//                            redisTemplate.opsForList().set(key, 0, restMoney);
//                            redisTemplate.opsForList().set(key, 1, restSize);
                            log.info("money = {}", money);
                            //用户抢到红包更新账户余额
                            userService.updateUserifo(id, money);
                            //更新红包数据

                            redEnvelopService.updateEnvelop(redEnvelopcurrent, restMoney, restSize);
                            //更新红包明细
                            redDetailService.updateRedDetail(redEnvelopcurrent, money, rid, id);
                            //return 成功抢到红包
                            return ResultUtil.result(ResultEnum.REDGET_SUCCESS);
                        } catch (Exception e) {
                            return ResultUtil.result(ResultEnum.FAIL);
                        }


                    }

                } catch (Exception e) {
                    return ResultUtil.result(ResultEnum.REDCOUNT_NO);
                }
            }
        }
    }
}

