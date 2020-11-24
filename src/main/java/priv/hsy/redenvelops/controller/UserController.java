package priv.hsy.redenvelops.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import priv.hsy.redenvelops.entity.*;
import priv.hsy.redenvelops.enums.ResultEnum;
import priv.hsy.redenvelops.service.*;
import priv.hsy.redenvelops.utils.ResultUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@Api( tags = "红包管理接口模块")
public class UserController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserService userService;
    @Autowired
    private RedEnvelopService redEnvelopService;
    @Autowired
    private RedDetailService redDetailService;
    @Autowired
    private RedisService redisService;

    @GetMapping(value = "/getuser")
    public List<User> getUser() {
        List<User> userList = userService.selectAll();
        log.info("userList = [{}]", userList);
        return userList;
    }
    /**
     * 分页显示所有设置红包详情
     *
     * @param currentPage
     * @param pageSize
     * @return
     */
    @PostMapping(value = "/api/getpageredinfo")
    @ApiOperation(value = "分页查询所有红包设置红包详情")
    public Result<Object> getPageRedInfo(@ApiParam(value = "当前页") @RequestParam("currentPage") Integer currentPage,
                                         @ApiParam(value = "每页大小") @RequestParam("pageSize") Integer pageSize) {

        RedEnvelopPageBean redEnvelopPageBean = redEnvelopService.selectPageRedInfo(currentPage, pageSize);
        log.info("list = [{}]", redEnvelopPageBean.getPageRecode());
        return ResultUtil.result(ResultEnum.SUCCESS, redEnvelopPageBean);
    }

    @PostMapping(value = "/api/getpageredenvelop")
    @ApiOperation(value = "分页查询所有正在发送的红包详情")
    public Result<Object> getPageRedEnvelop(@ApiParam(value = "当前页") @RequestParam("currentPage") Integer currentPage,
                                            @ApiParam(value = "每页大小") @RequestParam("pageSize") Integer pageSize) {

        RedEnvelopPageBean redEnvelopPageBean = redEnvelopService.selectPage(currentPage, pageSize);
        log.info("list = [{}]", redEnvelopPageBean.getPageRecode());
        return ResultUtil.result(ResultEnum.SUCCESS, redEnvelopPageBean);
    }

    /**
     * 创建红包接口
     *
     * @param redEnvelop 获取前端form表信息
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/setred")
    @ApiOperation(value = "创建红包")
    public Result<Object> setRed(@RequestBody RedEnvelop redEnvelop) {
        int uid = redEnvelop.getSendId();
        User user = userService.selectById(uid);
        double money = user.getMoney();
        //红包金额下限
        if (redEnvelop.getTotalMoney() < 0.01) {
//            return "红包金额最小值为0.01";
            return ResultUtil.result(ResultEnum.REDMONEY_MIN);
            //红包数量下限
        } else if (redEnvelop.getCount() < 1) {
//            return "红包数量最小为1";
            return ResultUtil.result(ResultEnum.REDCOUNT_MIN);
            //用户余额是否不足
        } else if (money < redEnvelop.getTotalMoney()) {
//            return "你的余额不足，无法设置红包，请加班挣钱！";
            return ResultUtil.result(ResultEnum.USERMONEY_NO);
        } else {//发红包用户余额更新
            double restmoney = money - redEnvelop.getTotalMoney();
            user.setMoney(restmoney);
            userService.updateById(user);
            return redEnvelopService.setRed(redEnvelop);
        }
    }

    /**
     * 更新已设置的红包
     *
     * @param rid        红包ID
     * @param count      红包数量
     * @param totalMoney 红包金额
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/updatered")
    @ApiOperation(value = "更新已设置的红包")
    public Result<Object> updateRed(@ApiParam(value = "红包id") @RequestParam("rid") Integer rid,
                                    @ApiParam(value = "红包数量") @RequestParam("count") Integer count,
                                    @ApiParam(value = "红包金额") @RequestParam("totalMoney") Double totalMoney) {
        RedEnvelop redEnvelop = redEnvelopService.selectById(rid);
        User user = userService.selectById(redEnvelop.getSendId());
        if (totalMoney < 0.01) {
//            return "红包金额最小值为0.01";
            return ResultUtil.result(ResultEnum.REDMONEY_MIN);
            //红包数量下限
        } else if (count < 1) {
//            return "红包数量最小为1";
            return ResultUtil.result(ResultEnum.REDCOUNT_MIN);
            //用户余额是否不足
        } else if (totalMoney > redEnvelop.getTotalMoney() + user.getMoney()) {
//            return "你的余额不足，无法设置红包！";
            return ResultUtil.result(ResultEnum.USERMONEY_NO);
        } else {
            //红包信息更新
            double restmoney = (totalMoney - redEnvelop.getTotalMoney()) + user.getMoney();
            redEnvelopService.updateEnvelop(redEnvelop, count, totalMoney);
            //发红包用户余额更新
            user.setUid(redEnvelop.getSendId());
            user.setMoney(restmoney);
            userService.updateById(user);
            return ResultUtil.result(ResultEnum.REDSET_SUCCESS);
        }
    }

    /**
     * 发送红包接口
     *
     * @param rid 红包id
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/sendred")
    @ApiOperation(value = "发送红包")
    public Result<Object> sendRed(@ApiParam(value = "红包id") @RequestParam("rid") Integer rid) {
        redEnvelopService.sendRed(rid);
        //return "成功发送红包！"
        return ResultUtil.result(ResultEnum.REDSEND_SUCCESS);
    }

    /**
     * 抢红包接口
     *
     * @param rid 获取前端红包id
     * @param uid  获取前端抢红包用户id
     * @return 返回状态码和消息提示
     */
    @PostMapping(value = "/api/getred")
    @ApiOperation(value = "抢红包")
    public Result<Object> getRed(@ApiParam(value = "红包id") @RequestParam("rid") Integer rid,
                                 @ApiParam(value = "用户id") @RequestParam("uid") Integer uid) {
        String key = rid + "redMoneylist";
        String redInfoCount = rid + "redInfoCount";
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "first", 1, TimeUnit.SECONDS);
        if (Boolean.FALSE.equals(lock)) {
            return ResultUtil.result(ResultEnum.FAIL, "lock");
        }
        try {
//            QueryWrapper<RedDetail> queryWrapper = new QueryWrapper<>();
//            queryWrapper.and(i -> i
//                    .eq("rid", rid)
//                    .eq("receive_id", uid));
//            if (redDetailService.selectOne(queryWrapper) != null) {
////                return "您已经抢过红包了";
//                return ResultUtil.result(ResultEnum.USERGET_NO);
//            }
            Integer restSize = (Integer) redisTemplate.opsForValue().get(redInfoCount);
            log.info("restSize = {}", restSize);
            if (restSize > 0) {
                redisTemplate.opsForValue().decrement(redInfoCount);
                Double money = (Double) redisTemplate.boundListOps(key).rightPop();
                String usergetkey = rid + "get" + uid;
                assert money != null;
                Boolean lock1 = redisTemplate.opsForValue().setIfAbsent(usergetkey, money);
                if (Boolean.FALSE.equals(lock1)) {
                    redisTemplate.opsForValue().increment(redInfoCount);
                    redisTemplate.opsForList().rightPush(key, money);
                    return ResultUtil.result(ResultEnum.USERGET_NO);
                }
                //红包明细
                redDetailService.insert(uid, rid, money);
                //更新红包剩余数量
                redEnvelopService.update(rid,money);
                //用户抢到红包更新账户余额
                userService.updateUserinfo(uid, money);

                if(restSize == 1){
                    redEnvelopService.overRed(rid);
                }
                return ResultUtil.result(ResultEnum.REDGET_SUCCESS);
            }

        } catch (Exception e) {
            return ResultUtil.result(ResultEnum.FAIL);
        } finally {
            redisTemplate.delete("lock");
        }
        return null;
    }

    @PostMapping(value = "/api/getrednouid")
    public Result<Object> test(@ApiParam(value = "红包id") @RequestParam("rid") Integer rid) {
        String key = rid + "redMoneylist";
        String redInfoCount = rid + "redInfoCount";
        String redInfoMoney = rid + "redInfoMoney";
        return redisService.redRedisGetNoUid(key, redInfoCount, redInfoMoney);
    }

    /**
     * 获得红包明细
     * @param rid 红包id
     * @return
     */
    @PostMapping(value = "/api/getreddetails")
    @ApiOperation(value = "添加红包明细")
    public Result<Object> getRedDetails(@ApiParam(value = "红包id") @RequestParam("rid") Integer rid) {
        List<RedDetail> redDetail = redDetailService.selectDetails(rid);
        return ResultUtil.result(ResultEnum.SUCCESS, redDetail);

    }

    @PostMapping(value = "/api/test")
    @ApiOperation(value = "抢红包")
    public Result<Object> test(@ApiParam(value = "红包id") @RequestParam("rid") Integer rid,
                               @ApiParam(value = "用户id") @RequestParam("uid") Integer uid) {
        String key = rid + "redMoneylist";
        String userGet = rid + "get" + uid;
        String redStatus = rid + "redStatus";
//        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "first", 1, TimeUnit.SECONDS);
        if (!(redisTemplate.hasKey(redStatus))) {
            if (!(redisTemplate.hasKey(userGet))) {
                if(redisTemplate.hasKey(key)){
                    Double money = (Double) redisTemplate.boundListOps(key).rightPop();
                    String usergetkey = rid + "get" + uid;
                    redisTemplate.opsForValue().setIfAbsent(usergetkey, money,1, TimeUnit.DAYS);
                    //红包明细
                    redDetailService.insert(uid, rid, money);
                    //更新红包剩余数量
                    redEnvelopService.update(rid,money);
                    //用户抢到红包更新账户余额
                    userService.updateUserinfo(uid, money);
                    return ResultUtil.result(ResultEnum.REDGET_SUCCESS);
                }
                else{
                    redEnvelopService.overRed(rid);
                    redisTemplate.opsForValue().set(redStatus,"红包已抢完！");
                    return ResultUtil.result(ResultEnum.REDCOUNT_NO);
                }
            }else{
                return ResultUtil.result(ResultEnum.USERGET_NO);
            }
        }else{
            return ResultUtil.result(ResultEnum.FAIL,"红包已抢完！");

        }

    }
}

